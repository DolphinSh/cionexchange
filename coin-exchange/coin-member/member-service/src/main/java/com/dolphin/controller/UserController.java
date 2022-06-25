package com.dolphin.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.User;
import com.dolphin.domain.UserAuthAuditRecord;
import com.dolphin.domain.UserAuthInfo;
import com.dolphin.domain.UserBank;
import com.dolphin.dto.UserDto;
import com.dolphin.feign.UserServiceFeign;
import com.dolphin.model.*;
import com.dolphin.service.UserAuthAuditRecordService;
import com.dolphin.service.UserAuthInfoService;
import com.dolphin.service.UserService;
import com.dolphin.vo.UseAuthInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
@Api(tags = "会员的控制器")
public class UserController implements UserServiceFeign {
    @Autowired
    private UserService userService;

    @Autowired
    private UserAuthAuditRecordService userAuthAuditRecordService;

    @Autowired
    private UserAuthInfoService userAuthInfoService;

    @GetMapping
    @ApiOperation(value = "查询会员的列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数"),
            @ApiImplicitParam(name = "mobile", value = "会员的手机号"),
            @ApiImplicitParam(name = "userId", value = "会员的Id,精确查询"),
            @ApiImplicitParam(name = "userName", value = "会员的名称"),
            @ApiImplicitParam(name = "realName", value = "会员的真实名称"),
            @ApiImplicitParam(name = "status", value = "会员的状态")

    })
    @PreAuthorize("hasAuthority('user_query')")
    public R<Page<User>> findByPage(@ApiIgnore Page<User> page,
                                    String mobile,
                                    Long userId,
                                    String userName,
                                    String realName,
                                    Integer status
    ) {
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<User> userPage = userService.findByPage(page, mobile, userId, userName, realName, status, null);
        return R.ok(userPage);
    }

    @PostMapping("/status")
    @ApiOperation(value = "修改用户的状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "会员的id"),
            @ApiImplicitParam(name = "status", value = "会员的状态"),
    })
    @PreAuthorize("hasAuthority('user_update')")
    public R updateStatus(Long id, Byte status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        boolean updateById = userService.updateById(user);
        if (updateById) {
            return R.ok("更新成功");
        }
        return R.fail("更新失败");
    }


    @PatchMapping
    @ApiOperation(value = "修改用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user", value = "会员的json数据"),
    })
    @PreAuthorize("hasAuthority('user_update')")
    public R updateStatus(@RequestBody @Validated User user) {
        boolean updateById = userService.updateById(user);
        if (updateById) {
            return R.ok("更新成功");
        }
        return R.fail("更新失败");
    }

    @GetMapping("/info")
    @ApiOperation(value = "查询会员的详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "会员的Id")
    })
    public R<User> userInfo(Long id) {
        User user = userService.getById(id);
        return R.ok(user);
    }

    @GetMapping("/directInvites")
    @ApiOperation(value = "查询该用户邀请的用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "该用户的Id"),
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数"),

    })
    public R<Page<User>> getDirectInvites(@ApiIgnore Page<User> page, Long userId) {
        Page<User> userPage = userService.findDirectInvitePage(page, userId);
        return R.ok(userPage);
    }

    @GetMapping("/auths")
    @ApiOperation(value = "查询用户的审核列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数"),
            @ApiImplicitParam(name = "mobile", value = "会员的手机号"),
            @ApiImplicitParam(name = "userId", value = "会员的Id,精确查询"),
            @ApiImplicitParam(name = "userName", value = "会员的名称"),
            @ApiImplicitParam(name = "realName", value = "会员的真实名称"),
            @ApiImplicitParam(name = "reviewsStatus", value = "会员的状态")

    })
    public R<Page<User>> findUserAuths(
            @ApiIgnore Page<User> page,
            String mobile,
            Long userId,
            String userName,
            String realName,
            Integer reviewsStatus
    ) {
        Page<User> userPage = userService.findByPage(page, mobile, userId, userName, realName, null, reviewsStatus);
        return R.ok(userPage);
    }

    /**
     * 查询用户的认证详情
     * {
     * user:
     * userAuthInfoList:[]
     * userAuditRecordList:[]
     * }
     *
     * @param id
     * @return
     */
    @GetMapping("/auth/info")
    @ApiOperation(value = "查询用户的认证详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户的Id")
    })
    public R<UseAuthInfoVo> getUseAuthInfo(Long id) {
        User user = userService.getById(id);
        List<UserAuthAuditRecord> userAuthAuditRecordList = null;
        List<UserAuthInfo> userAuthInfoList = null;
        if (user != null) {
            //查询用户的审核记录
            Integer reviewsStatus = user.getReviewsStatus();
            if (reviewsStatus == null || reviewsStatus == 0) {
                //等待审核，没有历史记录
                userAuthAuditRecordList = Collections.emptyList();
                //没有认证过，通过用户的id查询
                userAuthInfoList = userAuthInfoService.getUserAuthInfoByUserId(id);
            } else {
                userAuthAuditRecordList = userAuthAuditRecordService.getUserAuthAuditRecordList(id);
                //查询用户认证详情列表 用户的身份信息
                UserAuthAuditRecord userAuthAuditRecord = userAuthAuditRecordList.get(0); //之前是按照认证的日志排序的，第0个值，就是最近被认证的一个值
                Long authCode = userAuthAuditRecord.getAuthCode(); //认证的位置信息
                userAuthInfoList = userAuthInfoService.getUserAuthInfoByCode(authCode);
            }
        }
        return R.ok(new UseAuthInfoVo(user, userAuthInfoList, userAuthAuditRecordList));
    }

    /**
     * 审核的本质:
     * 在于对一组图片(唯一Code)的认可,符合条件,审核通过
     *
     * @return
     */
    @PostMapping("/auths/status")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户的ID"),
            @ApiImplicitParam(name = "authStatus", value = "用户的审核状态"),
            @ApiImplicitParam(name = "authCode", value = "一组图片的唯一标识"),
            @ApiImplicitParam(name = "remark", value = "审核拒绝的原因"),
    })
    public R updateUserAuthStatus(@RequestParam(required = true) Long id, @RequestParam(required = true) Byte authStatus, @RequestParam(required = true) Long authCode, String remark) {
        // 审核: 1 修改user 里面的reviewStatus
        // 2 在authAuditRecord 里面添加一条记录

        userService.updateUserAuthStatus(id, authStatus, authCode,remark);
        return R.ok();
    }

    @GetMapping("/current/info")
    @ApiOperation(value = "获取当前登录用户信息")
    public R<User> currentUserInfo(){
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userService.getById(Long.valueOf(userIdStr));
        //屏蔽信息
        user.setPassword("*****");
        user.setPaypassword("*****");
        user.setAccessKeyId("*****");
        user.setAccessKeySecret("*****");
        return R.ok(user);
    }

    @PostMapping("/authAccount")
    @ApiOperation(value = "用户的实名认证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userAuthForm", value = "userAuthFormjson数据")
    })
    public R identifiyCheck(@RequestBody UserAuthForm userAuthForm){
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        boolean isOk = userService.identifyVerfiy(Long.valueOf(userIdStr),userAuthForm);
        if (isOk){
            return R.ok("实名认证成功！");
        }
        return R.fail("实名认证失败！");
    }

    @PostMapping("/authUser")
    @ApiOperation(value = "用户进行高级认证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "imgs", value = "用户的图片地址")
    })
    public R authUser(@RequestBody String [] imgs){
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        boolean isOk = userService.authUser(Long.valueOf(userIdStr), Arrays.asList(imgs));
        if (isOk){
            return R.ok("提交高级认证成功！");
        }
        return R.fail("提交高级认证失败！");
    }

    @PostMapping("/updatePhone")
    @ApiOperation(value = "修改手机号")
    public R updatePhone(@RequestBody @Validated UpdatePhoneParam updatePhoneParam) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        boolean isOk = userService.updatePhone(Long.valueOf(userIdStr),updatePhoneParam);
        if (isOk) {
            return R.ok("修改手机号成功！");
        }
        return R.fail("修改手机号失败！");
    }

    @GetMapping("/checkTel")
    @ApiOperation(value = "检查新的手机号是否可用,如可用,则给该新手机发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "新的手机号"),
            @ApiImplicitParam(name = "countryCode", value = "手机号的区域")
    })
    public R checkNewPhone(@RequestParam(required = true) String mobile, @RequestParam(required = true) String countryCode) {
        boolean isOk = userService.checkNewPhone(mobile, countryCode);
        return isOk ? R.ok("新的手机号校验成功") : R.fail("新的手机号校验失败");
    }

    @PostMapping("/updateLoginPassword")
    @ApiOperation(value = "修改用户的登录密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "updateLoginParam", value = "修改用户的登录密码")
    })
    public R updateLoginPwd(@RequestBody @Validated UpdateLoginParam updateLoginParam) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        boolean isOk = userService.updateUserLoginPwd(userId, updateLoginParam);
        return isOk ? R.ok("修改用户的登录密码成功") : R.fail("修改用户的登录密码失败");
    }

    @PostMapping("/updatePayPassword")
    @ApiOperation(value = "修改用户的交易密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "updateLoginParam", value = "修改用户的交易密码")
    })
    public R updatePayPwd(@RequestBody @Validated UpdateLoginParam updateLoginParam) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        boolean isOk = userService.updateUserPayPwd(userId, updateLoginParam);
        return isOk ? R.ok("修改用户的交易密码成功") : R.fail("修改用户的交易密码失败");
    }

    @PostMapping("/setPayPassword")
    @ApiOperation(value = "重新设置交易密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "UnsetPayPasswordParam", value = "重新设置交易密码")
    })
    public R setPayPassword(@RequestBody @Validated UnsetPayPasswordParam unsetPayPasswordParam) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        boolean isOk = userService.unsetPayPassword(userId, unsetPayPasswordParam);
        return isOk ? R.ok("重新设置交易密码成功") : R.fail("重新设置交易密码失败");
    }

    /**
     * 用于admin-service里面 远程调用member-service
     * @param ids
     * @return
     */
    @Override
    public List<UserDto> getBasicUsers(List<Long> ids) {
        List<UserDto> userDto = userService.getBasicUsers(ids);
        return userDto;
    }
}
