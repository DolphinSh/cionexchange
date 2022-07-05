package com.dolphin.disruptor;


import com.dolphin.match.MatchServiceFactory;
import com.dolphin.match.MatchStrategy;
import com.dolphin.model.Order;
import com.dolphin.model.OrderBooks;
import com.lmax.disruptor.EventHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class OrderEventHandler implements EventHandler<OrderEvent> {

    private OrderBooks orderBooks;

    private String symbol;

    public OrderEventHandler(OrderBooks orderBooks) {
        this.orderBooks = orderBooks;
        this.symbol = this.orderBooks.getSymbol();
    }

    @Override
    public void onEvent(OrderEvent orderEvent, long sequence, boolean endOfBatch) throws Exception {
        log.info("开始接收订单事件 ======> {}", orderEvent);
        MatchServiceFactory.getMatchService(MatchStrategy.LIMIT_PRICE).match((Order) orderEvent.getSource());
        log.info("处理完成我们的订单事件===================>{}", orderEvent);
    }
}
