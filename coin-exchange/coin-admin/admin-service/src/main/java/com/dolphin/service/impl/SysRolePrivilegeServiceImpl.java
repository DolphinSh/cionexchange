package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dolphin.domain.SysMenu;
import com.dolphin.domain.SysPrivilege;
import com.dolphin.model.RolePrivilegesParam;
import com.dolphin.service.SysMenuService;
import com.dolphin.service.SysPrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.SysRolePrivilegeMapper;
import com.dolphin.domain.SysRolePrivilege;
import com.dolphin.service.SysRolePrivilegeService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class SysRolePrivilegeServiceImpl extends ServiceImpl<SysRolePrivilegeMapper, SysRolePrivilege> implements SysRolePrivilegeService {

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysPrivilegeService sysPrivilegeService;

    @Autowired
    private SysRolePrivilegeService sysRolePrivilegeService;

    /**
     * 查询角色的权限
     *
     * @param roleId
     * @return
     */
    @Override
    public List<SysMenu> findSysMenuAndPrivileges(Long roleId) {
        // 1-首先查询二级菜单
        List<SysMenu> list = sysMenuService.list();
        // 1.1 我们在页面上显示的二级菜单，以及二级菜单所包含我的权限
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<SysMenu> rootMenus = list.stream().filter(sysMenu -> sysMenu.getParentId() == null)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rootMenus)) {
            return Collections.emptyList();
        }
        //查询所有的二级菜单
        List<SysMenu> subMenus = new ArrayList<>();
        for (SysMenu rootMenu : rootMenus) {
            subMenus.addAll(getChildMenus(rootMenu.getId(), roleId, list));
        }
        return subMenus;
    }

    /**
     * 给角色授予权限
     *
     * @param rolePrivilegesParam
     * @return
     */
    @Transactional
    @Override
    public boolean grantPrivileges(RolePrivilegesParam rolePrivilegesParam) {
        //获取角色Id
        Long roleId = rolePrivilegesParam.getRoleId();
        //1 先删除原有角色的权限
        sysRolePrivilegeService.remove(new LambdaQueryWrapper<SysRolePrivilege>().eq(SysRolePrivilege::getRoleId, roleId));
        //2 新增该角色的权限
        List<Long> privilegeIds = rolePrivilegesParam.getPrivilegeIds();
        if (!CollectionUtils.isEmpty(privilegeIds)) {
            List<SysRolePrivilege> sysRolePrivilegeList = new ArrayList<>();
            //循环添加
            for (Long privilegeId : privilegeIds) {
                SysRolePrivilege sysRolePrivilege = new SysRolePrivilege();
                sysRolePrivilege.setRoleId(rolePrivilegesParam.getRoleId());
                sysRolePrivilege.setPrivilegeId(privilegeId);
                sysRolePrivilegeList.add(sysRolePrivilege);
            }
            boolean b = sysRolePrivilegeService.saveBatch(sysRolePrivilegeList);
            return b;
        }
        return true;
    }

    /**
     * 查询菜单的子菜单(递归)
     *
     * @param parentId 父id
     * @param roleId   当前查询的角色的id
     * @return
     */
    private List<SysMenu> getChildMenus(Long parentId, Long roleId, List<SysMenu> sources) {
        List<SysMenu> childrens = new ArrayList<>();
        for (SysMenu source : sources) {
            //递归找子孙代
            if (source.getParentId() == parentId) { //找儿子
                source.setChilds(getChildMenus(source.getId(), roleId, sources));//给该儿子设置儿子
                childrens.add(source);
                List<SysPrivilege> sysPrivileges = sysPrivilegeService.getAllSysPrivileges(source.getId(), roleId);
                source.setPrivileges(sysPrivileges);
            }
        }
        return childrens;
    }
}
