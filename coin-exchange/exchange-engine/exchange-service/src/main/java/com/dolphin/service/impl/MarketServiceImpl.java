package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.MarketMapper;
import com.dolphin.domain.Market;
import com.dolphin.service.MarketService;

@Service
public class MarketServiceImpl extends ServiceImpl<MarketMapper, Market> implements MarketService {

    /**
     * 交易市场的分页查询
     *
     * @param page        分页参数
     * @param tradeAreaId 交易区域的ID
     * @param status      状态
     * @return
     */
    @Override
    public Page<Market> findByPage(Page<Market> page, Long tradeAreaId, Byte status) {
        return page(page,
                new LambdaQueryWrapper<Market>()
                        .eq(tradeAreaId != null, Market::getTradeAreaId, tradeAreaId)
                        .eq(status != null, Market::getStatus, status)
        );
    }
}
