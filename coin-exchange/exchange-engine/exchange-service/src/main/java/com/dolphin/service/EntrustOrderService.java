package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.EntrustOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dolphin.domain.ExchangeTrade;
import com.dolphin.param.OrderParam;
import com.dolphin.vo.TradeEntrustOrderVo;

public interface EntrustOrderService extends IService<EntrustOrder>{

    /**
     * 查询用户的委托记录
     * @param page 分页参数
     * @param userId 用户的id
     * @param symbol 交易对
     * @param type 交易类型
     * @return
     */
    Page<EntrustOrder> findByPage(Page<EntrustOrder> page, Long userId, String symbol, Integer type);

    /**
     * 查询历史的委托单记录
     * @param page 分页参数
     * @param symbol 交易对
     * @param userId 用户id
     * @return
     */
    Page<TradeEntrustOrderVo> getHistoryEntrustOrder(Page<EntrustOrder> page, String symbol, Long userId);

    /**
     * 查询未完成的委托单
     * @param page 分页参数
     * @param symbol 交易对
     * @param userId 用户id
     * @return
     */
    Page<TradeEntrustOrderVo> getEntrustOrder(Page<EntrustOrder> page, String symbol, Long userId);

    /**
     * 委托单的下单操作
     * @param userId
     * @param orderParam
     * @return
     */
    Boolean createEntrustOrder(Long userId, OrderParam orderParam);

    /**
     * 更新委托单数据
     * @param exchangeTrade
     */
    void doMatch(ExchangeTrade exchangeTrade);
}
