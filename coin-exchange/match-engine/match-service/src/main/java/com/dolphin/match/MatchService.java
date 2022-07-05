package com.dolphin.match;

import com.dolphin.model.Order;

/**
 * 撮合/交易的接口定义
 */
public interface MatchService {

    /**
     * 执行撮合交易
     * @param order
     */
    void match(Order order);
}
