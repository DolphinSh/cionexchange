package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.EntrustOrder;
import com.baomidou.mybatisplus.extension.service.IService;
public interface EntrustOrderService extends IService<EntrustOrder>{

    /**
     * 查询用户的委托记录
     * @param page 分页参数
     * @param userId 用户的id
     * @param symbol 交易对
     * @param type 交易类型
     * @return
     */
    Page<EntrustOrder> findByPage(Page<EntrustOrder> page, Long userId, String symbol, Integer type);
}
