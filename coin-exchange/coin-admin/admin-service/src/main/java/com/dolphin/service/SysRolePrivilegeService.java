package com.dolphin.service;

import com.dolphin.domain.SysMenu;
import com.dolphin.domain.SysRolePrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysRolePrivilegeService extends IService<SysRolePrivilege>{

    /**
     * 查询角色的权限
     * @param roleId
     * @return
     */
    List<SysMenu> findSysMenuAndPrivileges(Long roleId);
}
