package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.Coin;
import com.dolphin.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.AdminAddress;
import com.dolphin.mapper.AdminAddressMapper;
import com.dolphin.service.AdminAddressService;

@Service
public class AdminAddressServiceImpl extends ServiceImpl<AdminAddressMapper, AdminAddress> implements AdminAddressService {
    @Autowired
    private CoinService coinService;

    /**
     * 查询归集地址
     *
     * @param page   分页参数
     * @param coinId 币种ID
     * @return
     */
    @Override
    public Page<AdminAddress> findByPage(Page<AdminAddress> page, Long coinId) {
        return page(page, new LambdaQueryWrapper<AdminAddress>()
                .eq(coinId != null, AdminAddress::getCoinId, coinId)
        );
    }

    /**
     * 重写save ,解决归集地址没有包含币种类型
     *
     * @param entity
     * @return
     */
    @Override
    public boolean save(AdminAddress entity) {
        Long coinId = entity.getCoinId();
        Coin coin = coinService.getById(coinId);
        if (coin == null) {
            throw new IllegalArgumentException("输入币种id错误！");
        }
        entity.setCoinType(coin.getType());
        return super.save(entity);
    }
}
