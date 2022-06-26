package com.dolphin.service;

import com.dolphin.domain.CoinConfig;
import com.baomidou.mybatisplus.extension.service.IService;
public interface CoinConfigService extends IService<CoinConfig>{

    /**
     * 查询币种的配置信息
     * @param coinId 币种的id值
     * @return
     */
    CoinConfig findByCoinId(Long coinId);
}
