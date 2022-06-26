package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.Coin;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CoinService extends IService<Coin>{

    /**
     * 分页条件查询数字货币
     * @param name 数字货币的名称
     * @param type  数字货币类型的名称
     * @param status 数字货币的状态
     * @param title 数字货币的标题
     * @param walletType 数字货币的钱包类型名称
     * @param page 分页参数
     * @return 数据货币的分页数据
     */
    Page<Coin> findByPage(String name, String type, Byte status, String title, String walletType, Page<Coin> page);

    /**
     * 使用币种的状态查询所有的币种信息
     * @param status 币种的状态
     * @return
     */
    List<Coin> getCoinsByStatus(Byte status);
}
