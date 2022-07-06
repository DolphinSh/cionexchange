package com.dolphin.match.impl;

import com.dolphin.enums.OrderDirection;
import com.dolphin.match.MatchService;
import com.dolphin.match.MatchServiceFactory;
import com.dolphin.match.MatchStrategy;
import com.dolphin.model.*;
import com.dolphin.rocket.Source;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.ast.Or;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class LimitPriceMatchServiceImpl implements MatchService, InitializingBean {

    @Autowired
    private Source source;
    /**
     * 执行撮合交易
     *
     * @param orderBooks
     * @param order
     */
    @Override
    public void match(OrderBooks orderBooks, Order order) {
        log.info("开始撮合！");
        //1. 进行数据的校验
        if (order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        //2 进行交易
        Iterator<Map.Entry<BigDecimal, MergeOrder>> markerQueueIterator = null;
        if (order.getOrderDirection() == OrderDirection.BUY) {
            //获取挂单队列
            TreeMap<BigDecimal, MergeOrder> currentLimitPrices = orderBooks.getCurrentLimitPrices(OrderDirection.SELL);
        } else {
            orderBooks.getCurrentLimitPrices(OrderDirection.BUY);
        }
        //3 循环挂单队列
        boolean exitLoop = false;
        List<Order> completedOrders = new ArrayList<>();//已经完成的订单
        List<ExchangeTrade> exchangeTrades = new ArrayList<>(); //产生的交易记录
        while (markerQueueIterator.hasNext()) {
            Map.Entry<BigDecimal, MergeOrder> markerOrderEntry = markerQueueIterator.next();
            BigDecimal marketPrice = markerOrderEntry.getKey();
            MergeOrder mergeMergeOrder = markerOrderEntry.getValue();
            //花10块买东西，别人的东西大于10块，就买不了
            if (order.getOrderDirection() == OrderDirection.BUY && order.getPrice().compareTo(marketPrice) < 0) {
                break;
            }

            if (order.getOrderDirection() == OrderDirection.SELL && order.getPrice().compareTo(marketPrice) > 0) {
                break;
            }
            Iterator<Order> markerIterator = mergeMergeOrder.iterator();
            while (markerIterator.hasNext()) {
                Order marker = markerIterator.next();
                ExchangeTrade exchangeTrade = processMath(order, marker, orderBooks);
                if (order.isCompleted()) {
                    completedOrders.add(order);
                    //退出循环挂单序列
                    exitLoop = true; //退出最外层循环
                    break;
                }
                if (marker.isCompleted()) { // MergeOrder的一个小的订单完成了
                    completedOrders.add(marker);
                    markerIterator.remove();
                }
            }
            if (mergeMergeOrder.size() == 0) { //MergeOrder 已经吃完
                markerQueueIterator.remove();//将该MergeOrder从树上移除掉
            }
        }

        //4 若订单没有完成
        if (order.getAmount().compareTo(order.getTradedAmount()) > 0) {
            orderBooks.addOrder(order);
        }

        //5 发送交易记录
        //处理交易记录
        handlerExchangeTrades(exchangeTrades);
        if (completedOrders.size() > 0) {
            completedOrders(completedOrders);
            //发送盘口数据，更新盘口
            TradePlate tradePlate = order.getOrderDirection() == OrderDirection.BUY ?
                    orderBooks.getBuyTradePlate() : orderBooks.getSellTradePlate();
            sendTradePlateData(tradePlate);
        }


        orderBooks.addOrder(order);
    }

    /**
     * 发送盘口数据，供前端进行数据更新
     *
     * @param tradePlate
     */
    private void sendTradePlateData(TradePlate tradePlate) {
        Message<TradePlate> build = MessageBuilder.
                withPayload(tradePlate).setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();
        source.plateOut().send(build);
    }

    /**
     * 订单的完成
     *
     * @param completedOrders
     */
    private void completedOrders(List<Order> completedOrders) {
        Message<List<Order>> build = MessageBuilder.
                withPayload(completedOrders).setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();
        source.completedOrdersOut().send(build);
    }

    /**
     * 处理订单记录
     *
     * @param exchangeTrades
     */
    private void handlerExchangeTrades(List<ExchangeTrade> exchangeTrades) {
        Message<List<ExchangeTrade>> build = MessageBuilder.
                withPayload(exchangeTrades).setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();
        source.exchangeTradesOut().send(build);
    }

    /**
     * 进行委托单的匹配撮合交易
     *
     * @param tracker 吃单
     * @param marker  挂单
     * @return 交易记录
     */
    private ExchangeTrade processMath(Order tracker, Order marker, OrderBooks orderBooks) {
        //1 定义交易的变量
        //成交的价格
        BigDecimal dealPrice = marker.getPrice();
        //成交的数量
        BigDecimal turnoverAmount = BigDecimal.ZERO;
        //本次需要的数量
        BigDecimal needAmount = calcTradeAmount(tracker);
        //本次提供给你的数量
        BigDecimal providerAmount = calcTradeAmount(tracker);

        turnoverAmount = needAmount.compareTo(providerAmount) <= 0 ? needAmount : providerAmount;

        if (turnoverAmount.compareTo(BigDecimal.ZERO) == 0) {
            return null; //无法成交
        }
        //设置成交额度
        tracker.setAmount(tracker.getTradedAmount().add(turnoverAmount));
        BigDecimal takerTurnover = turnoverAmount.multiply(dealPrice).setScale(orderBooks.getCoinScale(), RoundingMode.HALF_UP);
        tracker.setTurnover(takerTurnover);

        marker.setTradedAmount(marker.getTradedAmount().add(turnoverAmount));
        BigDecimal markerTurnover = turnoverAmount.multiply(dealPrice).setScale(orderBooks.getCoinScale(), RoundingMode.HALF_UP);
        marker.setTurnover(markerTurnover);

        ExchangeTrade exchangeTrade = new ExchangeTrade();
        exchangeTrade.setAmount(turnoverAmount); //设置购买的数量
        exchangeTrade.setPrice(dealPrice); //设置购买的价格
        exchangeTrade.setTime(System.currentTimeMillis()); //设置交易时间
        exchangeTrade.setSymbol(orderBooks.getSymbol());//设置交易对
        exchangeTrade.setDirection(tracker.getOrderDirection());//设置交易的方向
        exchangeTrade.setSellOrderId(tracker.getOrderId());//设置出售方的id
        exchangeTrade.setBuyTurnover(tracker.getTurnover());//设置买房的id
        exchangeTrade.setSellTurnover(marker.getTurnover());//设置卖方的交易额

        if (tracker.getOrderDirection() == OrderDirection.BUY) {
            orderBooks.getBuyTradePlate().remove(marker, turnoverAmount);
        } else {
            orderBooks.getSellTradePlate().remove(marker, turnoverAmount);
        }
        return exchangeTrade;
    }

    /**
     * 计算本次实时交易额
     *
     * @param tracker
     * @return
     */
    private BigDecimal calcTradeAmount(Order tracker) {
        return tracker.getAmount().subtract(tracker.getTradedAmount());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MatchServiceFactory.addMatchService(MatchStrategy.LIMIT_PRICE, this);
    }

}
