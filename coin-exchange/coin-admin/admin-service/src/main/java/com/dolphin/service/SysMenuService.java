package com.dolphin.service;

import com.dolphin.domain.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysMenuService extends IService<SysMenu>{

    /**
     * 通过用户的id查询用户的菜单数据
     * @param userId
     * @return
     */
    List<SysMenu> getMenusByUserId(Long userId);
}
