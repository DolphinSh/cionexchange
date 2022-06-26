package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.CoinConfigMapper;
import com.dolphin.domain.CoinConfig;
import com.dolphin.service.CoinConfigService;
@Service
public class CoinConfigServiceImpl extends ServiceImpl<CoinConfigMapper, CoinConfig> implements CoinConfigService{

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
                .eq(coinId != null,CoinConfig::getId,coinId)
        );
    }
}
