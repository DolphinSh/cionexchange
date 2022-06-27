package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.TradeArea;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TradeAreaService extends IService<TradeArea>{

    /**
     * 分页查询交易区域
     * @param page 分页参数
     * @param name 交易区域的名称
     * @param status 交易区域的状态
     * @return
     */
    Page<TradeArea> findByPage(Page<TradeArea> page, String name, Byte status);

    /**
     * 查询交易区域
     * @param status 状态
     * @return 查询交易区域结果
     */
    List<TradeArea> findAll(Byte status);
}
