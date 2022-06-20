package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.SysUserRole;
import com.dolphin.model.R;
import com.dolphin.service.SysUserRoleService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.SysUser;
import com.dolphin.mapper.SysUserMapper;
import com.dolphin.service.SysUserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserRoleService sysUserRoleService;


    @Autowired
    private SysUserService sysUserService;
    /**
     * 分页查询员工
     *
     * @param page     分页参数
     * @param mobile   员工手机
     * @param fullname 员工全名称
     * @return
     */
    @Override
    public Page<SysUser> findByPage(Page<SysUser> page, String mobile, String fullname) {
        Page<SysUser> pageData = page(page,
                new LambdaQueryWrapper<SysUser>()
                        .like(!StringUtils.isEmpty(mobile), SysUser::getMobile, mobile)
                        .like(!StringUtils.isEmpty(fullname), SysUser::getFullname, fullname)
        );
        List<SysUser> records = pageData.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            for (SysUser record : records) {
                //查询用户所有角色信息
                List<SysUserRole> userRoleList = sysUserRoleService.list(
                        new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, record.getId()));
                if (!CollectionUtils.isEmpty(userRoleList)) {
                    record.setRole_strings(userRoleList.stream().
                            map(sysUserRole -> sysUserRole.getRoleId().toString())
                            .collect(Collectors.joining(",")));
                }
            }
        }
        return pageData;
    }

    /**
     * 新增员工
     *
     * @param sysUser
     * @return
     */
    @Override
    @Transactional
    public boolean addUser(SysUser sysUser) {
        //用户的密码
        String password = sysUser.getPassword();
        //用户的角色Ids
        String role_strings = sysUser.getRole_strings();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(password);
        sysUser.setPassword(encode);
        boolean save = super.save(sysUser);
        //判断用户是否新增成功
        if (save) {
            //保存用户角色信息
            if (!StringUtils.isEmpty(role_strings)) {
                String[] roleIds = role_strings.split(",");
                List<SysUserRole> sysUserRoleList = new ArrayList<>();
                for (String roleId :
                        roleIds) {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setRoleId(Long.valueOf(roleId));
                    sysUserRole.setUserId(sysUser.getId());
                    sysUserRoleList.add(sysUserRole);
                }
                boolean b = sysUserRoleService.saveBatch(sysUserRoleList);
                if (b) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 更新员工
     *
     * @param sysUser
     * @return
     */
    @Override
    @Transactional
    public boolean updateUser(SysUser sysUser) {
        //用户的密码
        String password = sysUser.getPassword();
        //用户的角色Ids
        String role_strings = sysUser.getRole_strings();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(password);
        sysUser.setPassword(encode);
        boolean save = super.updateById(sysUser);
        //判断用户是否新增成功
        if (save) {
            //删除原有关系,有没有都要删一边
            boolean remove = sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>()
                    .eq(SysUserRole::getUserId, sysUser.getId()));
            //删除成功
            //保存用户角色信息
            if (!StringUtils.isEmpty(role_strings)) {
                String[] roleIds = role_strings.split(",");
                List<SysUserRole> sysUserRoleList = new ArrayList<>();
                for (String roleId :
                        roleIds) {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setRoleId(Long.valueOf(roleId));
                    sysUserRole.setUserId(sysUser.getId());
                    sysUserRoleList.add(sysUserRole);
                }
                boolean b = sysUserRoleService.saveBatch(sysUserRoleList);
                if (b) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        boolean b = super.removeByIds(idList);
        boolean remove = sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, idList));
        return remove;
    }

}
