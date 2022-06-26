package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.CoinMapper;
import com.dolphin.domain.Coin;
import com.dolphin.service.CoinService;

@Service
public class CoinServiceImpl extends ServiceImpl<CoinMapper, Coin> implements CoinService {

    /**
     * 分页条件查询数字货币
     *
     * @param name       数字货币的名称
     * @param type       数字货币类型的名称
     * @param status     数字货币的状态
     * @param title      数字货币的标题
     * @param walletType 数字货币的钱包类型名称
     * @param page       分页参数
     * @return 数据货币的分页数据
     */
    @Override
    public Page<Coin> findByPage(String name, String type, Byte status, String title, String walletType, Page<Coin> page) {
        return page(page,
                new LambdaQueryWrapper<Coin>()
                        .like(!StringUtils.isEmpty(name), Coin::getName, name) // 名称的查询
                        .like(!StringUtils.isEmpty(title), Coin::getTitle, title)  // 标题的查询
                        .eq(status != null, Coin::getStatus, status)  // 状态的查询
                        .eq(!StringUtils.isEmpty(type), Coin::getType, type) // 货币类型名称的查询
                        .eq(!StringUtils.isEmpty(walletType), Coin::getWallet, walletType) // 货币钱包类型的查询
        );
    }

    /**
     * 使用币种的状态查询所有的币种信息
     *
     * @param status 币种的状态
     * @return
     */
    @Override
    public List<Coin> getCoinsByStatus(Byte status) {
        return list(new LambdaQueryWrapper<Coin>()
                .eq(status != null, Coin::getStatus, status)
        );
    }
}
