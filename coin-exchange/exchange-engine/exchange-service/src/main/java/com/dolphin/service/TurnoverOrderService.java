package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.TurnoverOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TurnoverOrderService extends IService<TurnoverOrder>{

    /**
     * 查询分页对象
     * @param page 分页数据
     * @param userId 用户的ID
     * @param symbol 交易对
     * @param type 交易类型
     * @return
     */
    Page<TurnoverOrder> findByPage(Page<TurnoverOrder> page, Long userId, String symbol, Integer type);

    /**
     * 获取买入的订单的成功的记录
     * @param orderId
     * @param userId
     * @return
     */
    List<TurnoverOrder> getBuyTurnoverOrder(Long orderId, Long userId);

    /**
     * 获取卖出订单的成交记录
     * @param orderId
     * @return
     */
    List<TurnoverOrder> getSellTurnoverOrder(Long orderId,Long userId);
}
