package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.EntrustOrder;
import com.dolphin.mapper.EntrustOrderMapper;
import com.dolphin.service.EntrustOrderService;
@Service
public class EntrustOrderServiceImpl extends ServiceImpl<EntrustOrderMapper, EntrustOrder> implements EntrustOrderService{

    /**
     * 查询用户的委托记录
     *
     * @param page   分页参数
     * @param userId 用户的id
     * @param symbol 交易对
     * @param type   交易类型
     * @return
     */
    @Override
    public Page<EntrustOrder> findByPage(Page<EntrustOrder> page, Long userId, String symbol, Integer type) {
        return page(page,
                new LambdaQueryWrapper<EntrustOrder>()
                        .eq(EntrustOrder::getUserId, userId)
                        .eq(!StringUtils.isEmpty(symbol), EntrustOrder::getSymbol, symbol)
                        .eq(type != null && type != 0, EntrustOrder::getType, type)
                        .orderByDesc(EntrustOrder::getCreated)

        );
    }
}
