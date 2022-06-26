package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dolphin.domain.Coin;
import com.dolphin.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.CoinConfigMapper;
import com.dolphin.domain.CoinConfig;
import com.dolphin.service.CoinConfigService;

@Service
public class CoinConfigServiceImpl extends ServiceImpl<CoinConfigMapper, CoinConfig> implements CoinConfigService {

    @Autowired
    private CoinService coinService;

    /**
     * 查询币种的配置信息
     *
     * @param coinId 币种的id值
     * @return
     */
    @Override
    public CoinConfig findByCoinId(Long coinId) {
        return getOne(new LambdaQueryWrapper<CoinConfig>()
                // coinConfig的id 和Coin的id 值是相同的
                .eq(coinId != null, CoinConfig::getId, coinId)
        );
    }

    /**
     * 新增或修改币种配置
     *
     * @param coinConfig
     * @return
     */
    @Override
    public boolean updateOrSave(CoinConfig coinConfig) {
        Coin coin = coinService.getById(coinConfig.getId());
        if (coin == null) {
            throw new IllegalArgumentException("coin-Id 不存在！");
        }
        coinConfig.setCoinType(coin.getType());
        coinConfig.setName(coin.getName());
        CoinConfig config = getById(coinConfig.getId());
        //没有保存，有新增
        if (config == null) {
            return save(coinConfig);
        } else {
            return updateById(coinConfig);
        }
    }
}
