package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.SysPrivilegeMapper;
import com.dolphin.domain.SysPrivilege;
import com.dolphin.service.SysPrivilegeService;
import org.springframework.util.CollectionUtils;

@Service
public class SysPrivilegeServiceImpl extends ServiceImpl<SysPrivilegeMapper, SysPrivilege> implements SysPrivilegeService{

    @Autowired
    private SysPrivilegeMapper sysPrivilegeMapper;

    /**
     * 获取该菜单下的所有权限数据
     * @param menuId 菜单id
     * @param roleId 当前查询的角色id
     * @return
     */
    @Override
    public List<SysPrivilege> getAllSysPrivileges(Long menuId,Long roleId) {
        //1 查询该菜单下的所有权限
        List<SysPrivilege> sysPrivileges = list(new LambdaQueryWrapper<SysPrivilege>().eq(
                SysPrivilege::getMenuId, menuId
        ));
        if (CollectionUtils.isEmpty(sysPrivileges)){
            return Collections.emptyList();
        }
        //2 当前传递的角色使用包含该权限信息也要放进去
        for (SysPrivilege sysPrivilege:
             sysPrivileges) {
            Set<Long> currentRoleSysPrivilegeIds = sysPrivilegeMapper.getPrivilegeByRoleId(roleId);
            if (currentRoleSysPrivilegeIds.contains(sysPrivilege.getId())){
                sysPrivilege.setOwn(1);//当前的角色是否有该权限
            }
        }
        return sysPrivileges;
    }
}
