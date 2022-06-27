package com.dolphin.aspect.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.SysUser;
import com.dolphin.model.R;
import com.dolphin.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;

@Api(tags = "员工管理")
@RestController
@RequestMapping("/users")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数"),
            @ApiImplicitParam(name = "mobile", value = "员工的手机号码"),
            @ApiImplicitParam(name = "fullname", value = "员工的全名称"),
    })
    @PreAuthorize("hasAuthority('sys_user_query')")
    public R<Page<SysUser>> findByPage(@ApiIgnore Page<SysUser> page, String mobile, String fullname) {
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<SysUser> pageData = sysUserService.findByPage(page, mobile, fullname);
        return R.ok(pageData);
    }

    @PostMapping
    @ApiOperation(value = "新增员工")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysUser", value = "sysUser 的json数据")
    })
    @PreAuthorize("hasAuthority('sys_user_create')")
    public R addUser(@RequestBody SysUser sysUser) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        sysUser.setCreateBy(userId);
        boolean isOK = sysUserService.addUser(sysUser);
        if (isOK) {
            return R.ok("新增成功！");
        }
        return R.fail("新增失败！");
    }

    @PatchMapping
    @ApiOperation(value = "更新员工")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysUser", value = "sysUser 的json数据")
    })
    @PreAuthorize("hasAuthority('sys_user_update')")
    public R updateUser(@RequestBody SysUser sysUser) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        sysUser.setModifyBy(userId);
        boolean isOK = sysUserService.updateUser(sysUser);
        if (isOK) {
            return R.ok("更新成功!");
        }
        return R.fail("更新失败！");
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除用户")
    @PreAuthorize("hasAuthority('sys_user_delete')")
    public R deleteUser(@RequestBody Long ids[]) {
        if (ids != null && ids.length > 0) {
            boolean b = sysUserService.removeByIds(Arrays.asList(ids));
            if (b) {
                return R.ok("删除成功！");
            }
        }
        return R.fail("删除失败！");
    }
}
