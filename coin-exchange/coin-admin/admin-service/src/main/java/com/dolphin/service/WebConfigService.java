package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.WebConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface WebConfigService extends IService<WebConfig>{

    /**
     * 分页条件查询webConfig
     * @param page
     * @param name
     * @param type
     * @return
     */
    Page<WebConfig> findByPage(Page<WebConfig> page, String name, String type);

    /**
     * 获取pc端的banner图
     * @return
     */
    List<WebConfig> getPcBanners();
}
