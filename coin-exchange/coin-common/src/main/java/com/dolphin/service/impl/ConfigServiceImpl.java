package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.ConfigMapper;
import com.dolphin.domain.Config;
import com.dolphin.service.ConfigService;

@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    /**
     * 条件分页查询后台参数
     *
     * @param page
     * @param type
     * @param name
     * @param code
     * @return
     */
    @Override
    public Page<Config> findByPage(Page<Config> page, String type, String name, String code) {
        Page<Config> pageData = page(page, new LambdaQueryWrapper<Config>()
                .like(!StringUtils.isEmpty(type), Config::getType, type)
                .like(!StringUtils.isEmpty(code), Config::getCode, code)
                .like(!StringUtils.isEmpty(name), Config::getName, name)
        );
        return pageData;
    }

    /**
     * 通过规则的code查询签名
     *
     * @param code
     * @return
     */
    @Override
    public Config getConfigByCode(String code) {
        return getOne(new LambdaQueryWrapper<Config>().eq(Config::getCode,code));
    }
}
