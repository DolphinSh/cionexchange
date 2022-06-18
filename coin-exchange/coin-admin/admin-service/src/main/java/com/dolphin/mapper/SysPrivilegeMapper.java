package com.dolphin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dolphin.domain.SysPrivilege;

import java.util.Set;

public interface SysPrivilegeMapper extends BaseMapper<SysPrivilege> {
    /**
     * 根据角色Id 获取权限
     * @param roleId
     * @return
     */
    Set<Long> getPrivilegeByRoleId(Long roleId);
}