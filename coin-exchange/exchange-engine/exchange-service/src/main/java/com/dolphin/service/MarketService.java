package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.Market;
import com.baomidou.mybatisplus.extension.service.IService;
public interface MarketService extends IService<Market>{

    /**
     * 交易市场的分页查询
     * @param page 分页参数
     * @param tradeAreaId 交易区域的ID
     * @param status 状态
     * @return
     */
    Page<Market> findByPage(Page<Market> page, Long tradeAreaId, Byte status);
}
