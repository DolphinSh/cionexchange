package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.SysRole;
import com.dolphin.mapper.SysRoleMapper;
import com.dolphin.service.SysRoleService;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService{

    @Autowired
    private SysRoleMapper sysRoleMapper;


    /**
     * 判断一个用户是否为超级管理员
     *
     * @param userId
     * @return
     */
    @Override
    public boolean isSuperAdmin(Long userId) {
        //当用户的角色code 为: ROLE_ADMIN 该用户为超级管理员
        //用户的id -> 用户的角色 -> 该角色的Code 是否为ROLE_ADMIN
        System.out.println(userId);
        String roleCode = sysRoleMapper.getUserRoleCode(userId);
        if (!StringUtils.isEmpty(roleCode) && roleCode.equals("ROLE_ADMIN")) {
            return true;
        }
        return false;
    }

    /**
     * 根据角色的名称模糊分页查询角色
     *
     * @param page
     * @param name 角色名称
     * @return
     */
    @Override
    public Page<SysRole> findByPage(Page<SysRole> page, String name) {
        return page(page,new LambdaQueryWrapper<SysRole>().like(
                !StringUtils.isEmpty(name),SysRole::getName,name
        ));
    }
}
