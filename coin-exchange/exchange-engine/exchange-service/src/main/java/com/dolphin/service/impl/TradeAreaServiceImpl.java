package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.TradeArea;
import com.dolphin.mapper.TradeAreaMapper;
import com.dolphin.service.TradeAreaService;

@Service
public class TradeAreaServiceImpl extends ServiceImpl<TradeAreaMapper, TradeArea> implements TradeAreaService {

    /**
     * 分页查询交易区域
     *
     * @param page   分页参数
     * @param name   交易区域的名称
     * @param status 交易区域的状态
     * @return
     */
    @Override
    public Page<TradeArea> findByPage(Page<TradeArea> page, String name, Byte status) {
        return page(page, new LambdaQueryWrapper<TradeArea>()
                .eq(status != null, TradeArea::getStatus, status)
                .like(!StringUtils.isEmpty(name), TradeArea::getName, name)
        );
    }

    /**
     * 查询交易区域
     *
     * @param status 状态
     * @return 查询交易区域结果
     */
    @Override
    public List<TradeArea> findAll(Byte status) {
        return list(new LambdaQueryWrapper<TradeArea>()
                .eq(status != null, TradeArea::getStatus, status)
        );
    }
}
