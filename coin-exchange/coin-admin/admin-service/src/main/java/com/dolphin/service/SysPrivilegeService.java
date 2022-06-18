package com.dolphin.service;

import com.dolphin.domain.SysPrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysPrivilegeService extends IService<SysPrivilege>{

    /**
     * 获取该菜单下的所有权限数据
     * @param menuId 菜单id
     * @param roleId 当前查询的角色id
     * @return
     */
    List<SysPrivilege> getAllSysPrivileges(Long menuId,Long roleId);
}
