package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.CoinType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CoinTypeService extends IService<CoinType>{

    /**
     * 条件分页查询币种类型
     * @param page 分页参数
     * @param code 币种类型
     * @return
     */
    Page<CoinType> findByPage(Page<CoinType> page, String code);

    /**
     *  使用币种类型的状态查询所有的币种类型值
     * @param status  币种类型的状态
     * @return
     */
    List<CoinType> listByStatus(Byte status);
}
