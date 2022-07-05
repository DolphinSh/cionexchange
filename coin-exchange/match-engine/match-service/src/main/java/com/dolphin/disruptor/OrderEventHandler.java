package com.dolphin.disruptor;


import com.dolphin.match.MatchServiceFactory;
import com.dolphin.match.MatchStrategy;
import com.dolphin.model.Order;
import com.dolphin.model.OrderBooks;
import com.lmax.disruptor.EventHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 该对象有多个symbol的数据对应
 * 针对某一个Order的EventHandler，只会同一时间有一个线程来执行它
 */
@Data
@Slf4j
public class OrderEventHandler implements EventHandler<OrderEvent> {

    private OrderBooks orderBooks;

    private String symbol; //交易对

    public OrderEventHandler(OrderBooks orderBooks) {
        this.orderBooks = orderBooks;
        this.symbol = this.orderBooks.getSymbol();
    }

    @Override
    public void onEvent(OrderEvent orderEvent, long sequence, boolean endOfBatch) throws Exception {
        //从ringbuffer里面接收了某个数据
        Order order = (Order) orderEvent.getSource();
        if (!order.getSymbol().equals(symbol)){
            //接收到不属于该处理器的处理的数据，不进行处理
            return;
        }

        log.info("开始接收订单事件 ======> {}", orderEvent);
        MatchServiceFactory.getMatchService(MatchStrategy.LIMIT_PRICE).match(orderBooks,order);
        log.info("处理完成我们的订单事件===================>{}", orderEvent);
    }
}
