package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.Market;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MarketService extends IService<Market>{

    /**
     * 交易市场的分页查询
     * @param page 分页参数
     * @param tradeAreaId 交易区域的ID
     * @param status 状态
     * @return
     */
    Page<Market> findByPage(Page<Market> page, Long tradeAreaId, Byte status);

    /**
     * 使用交易区域Id 查询该区域下的市场
     * @param id
     * @return
     */
    List<Market> getMarkersByTradeAreaId(Long id);

    /**
     *使用交易对查询市场
     * @param symbol
     * @return
     */
    Market getMarkerBySymbol(String symbol);
}
