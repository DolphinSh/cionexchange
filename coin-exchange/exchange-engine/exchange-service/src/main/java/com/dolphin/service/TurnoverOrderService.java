package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.TurnoverOrder;
import com.baomidou.mybatisplus.extension.service.IService;
public interface TurnoverOrderService extends IService<TurnoverOrder>{

    /**
     * 查询分页对象
     * @param page 分页数据
     * @param userId 用户的ID
     * @param symbol 交易对
     * @param type 交易类型
     * @return
     */
    Page<TurnoverOrder> findByPage(Page<TurnoverOrder> page, Long userId, String symbol, Integer type);
}
