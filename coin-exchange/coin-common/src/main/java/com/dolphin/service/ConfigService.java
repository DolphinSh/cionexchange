package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.Config;
import com.baomidou.mybatisplus.extension.service.IService;
public interface ConfigService extends IService<Config>{

    /**
     * 条件分页查询后台参数
     * @param page
     * @param type
     * @param name
     * @param code
     * @return
     */
    Page<Config> findByPage(Page<Config> page, String type, String name, String code);

    /**
     * 通过规则的code查询签名
     * @param code 规则的code
     * @return
     */
    Config getConfigByCode(String code);
}
