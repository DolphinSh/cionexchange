package com.dolphin.service.impl;

import com.dolphin.domain.SysMenu;
import com.dolphin.domain.SysPrivilege;
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
import org.springframework.util.CollectionUtils;

@Service
public class SysRolePrivilegeServiceImpl extends ServiceImpl<SysRolePrivilegeMapper, SysRolePrivilege> implements SysRolePrivilegeService{

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysPrivilegeService sysPrivilegeService;
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
        if (CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }
        List<SysMenu> rootMenus = list.stream().filter(sysMenu -> sysMenu.getParentId() == null)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rootMenus)){
            return Collections.emptyList();
        }
        //查询所有的二级菜单
        List<SysMenu> subMenus = new ArrayList<>();
        for (SysMenu rootMenu: rootMenus){
            subMenus.addAll(getChildMenus(rootMenu.getId(),roleId,list));
        }
        return subMenus;
    }

    /**
     * 查询菜单的子菜单(递归)
     * @param parentId 父id
     * @param roleId 当前查询的角色的id
     * @return
     */
    private List<SysMenu> getChildMenus(Long parentId,Long roleId,List<SysMenu> sources) {
        List<SysMenu> childrens = new ArrayList<>();
        for (SysMenu source: sources) {
            //递归找子孙代
            if (source.getParentId()==parentId){ //找儿子
                source.setChilds(getChildMenus(source.getId(),roleId,sources));//给该儿子设置儿子
                childrens.add(source);
                List<SysPrivilege> sysPrivileges = sysPrivilegeService.getAllSysPrivileges(source.getId(),roleId);
                source.setPrivileges(sysPrivileges);
            }
        }
        return childrens;
    }
}
