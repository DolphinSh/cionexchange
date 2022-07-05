package com.dolphin.match;

import com.dolphin.model.Order;
import com.dolphin.model.OrderBooks;

/**
 * 撮合/交易的接口定义
 */
public interface MatchService {

    /**
     * 执行撮合交易
     * @param order
     */
    void match(OrderBooks orderBooks, Order order);
}
