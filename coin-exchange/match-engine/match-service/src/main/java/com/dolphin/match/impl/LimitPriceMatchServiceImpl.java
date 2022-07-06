package com.dolphin.match.impl;

import com.dolphin.enums.OrderDirection;
import com.dolphin.match.MatchService;
import com.dolphin.match.MatchServiceFactory;
import com.dolphin.match.MatchStrategy;
import com.dolphin.model.*;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.ast.Or;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class LimitPriceMatchServiceImpl implements MatchService, InitializingBean {

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
                ExchangeTrade exchangeTrade = processMath(order, marker);
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
        if (order.getAmount().compareTo(order.getTradedAmount()) > 0){
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
     * @param tradePlate
     */
    private void sendTradePlateData(TradePlate tradePlate) {
    }

    /**
     * 订单的完成
     * @param completedOrders
     */
    private void completedOrders(List<Order> completedOrders) {
    }

    /**
     * 处理订单记录
     * @param exchangeTrades
     */
    private void handlerExchangeTrades(List<ExchangeTrade> exchangeTrades) {
    }

    /**
     * 进行委托单的匹配撮合交易
     * @param tracker 吃单
     * @param marker 挂单
     * @return 交易记录
     */
    private ExchangeTrade processMath(Order tracker, Order marker) {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MatchServiceFactory.addMatchService(MatchStrategy.LIMIT_PRICE, this);
    }

}
