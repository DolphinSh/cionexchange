package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.CoinTypeMapper;
import com.dolphin.domain.CoinType;
import com.dolphin.service.CoinTypeService;

@Service
public class CoinTypeServiceImpl extends ServiceImpl<CoinTypeMapper, CoinType> implements CoinTypeService {

    /**
     * 条件分页查询币种类型
     *
     * @param page 分页参数
     * @param code 币种类型
     * @return
     */
    @Override
    public Page<CoinType> findByPage(Page<CoinType> page, String code) {
        return page(page, new LambdaQueryWrapper<CoinType>()
                .like(!StringUtils.isEmpty(code), CoinType::getCode, code)
        );
    }

    /**
     * 使用币种类型的状态查询所有的币种类型值
     *
     * @param status 币种类型的状态
     * @return
     */
    @Override
    public List<CoinType> listByStatus(Byte status) {
        return list(new LambdaQueryWrapper<CoinType>()
                .eq(status != null, CoinType::getStatus, status)
        );
    }
}
