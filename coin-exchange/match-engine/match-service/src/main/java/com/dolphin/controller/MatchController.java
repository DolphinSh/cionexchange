package com.dolphin.controller;

import com.dolphin.disruptor.OrderEvent;
import com.dolphin.disruptor.OrderEventHandler;
import com.dolphin.enums.OrderDirection;
import com.dolphin.match.MatchEngineAutoConfiguration;
import com.dolphin.model.MergeOrder;
import com.dolphin.model.OrderBooks;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.TreeMap;

@RestController
public class MatchController {

    @Autowired
    private EventHandler<OrderEvent>[] eventHandlers;

    @GetMapping("/match/order")
    public TreeMap<BigDecimal, MergeOrder> getTradeData(@RequestParam(required = true) String symbol, @RequestParam(required = true) Integer orderDirection) {
        for (EventHandler<OrderEvent> eventHandler : eventHandlers) {
            OrderEventHandler orderEventHandler = (OrderEventHandler) eventHandler;
            if (orderEventHandler.getSymbol().equals(symbol)) {
                OrderBooks orderBooks = orderEventHandler.getOrderBooks();
                return orderBooks.getCurrentLimitPrices(OrderDirection.getOrderDirection(orderDirection));
            }
        }
        return null;
    }
}
