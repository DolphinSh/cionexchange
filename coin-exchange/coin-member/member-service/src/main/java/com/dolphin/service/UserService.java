package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dolphin.dto.UserDto;
import com.dolphin.model.*;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User>{

    /**
     * 条件分页查询查询会员的列表
     * @param page
     * @param mobile 会员的手机号
     * @param userId 会员的Id
     * @param userName 会员的名称
     * @param realName 会员的真实名称
     * @param status 会员的状态
     * @param reviewsStatus 会员的审核状态
     * @return
     */
    Page<User> findByPage(Page<User> page, String mobile, Long userId, String userName, String realName, Integer status, Integer reviewsStatus);

    /**
     * 查询该用户邀请的用户列表
     * @param page 分页参数
     * @param userId 用户id
     * @return
     */
    Page<User> findDirectInvitePage(Page<User> page, Long userId);

    /**
     *  修改用户的审核状态
     * @param id
     * @param authStatus
     * @param authCode
     */
    void updateUserAuthStatus(Long id, Byte authStatus, Long authCode, String remark);

    /**
     * 用户实名认证
     * @param id 用户实名认证id
     * @param userAuthForm 用户表单
     * @return
     */
    boolean identifyVerfiy(Long id, UserAuthForm userAuthForm);

    /**
     * 用户进行高级认证
     * @param userId 用户id
     * @param imgList  用户的图片地址
     * @return 提交高级认证结果
     */
    boolean authUser(Long userId, List<String> imgList);

    /**
     * 修改用户的手机号
     * @param userId 用户id
     * @param updatePhoneParam 修改手机的参数
     * @return
     */
    boolean updatePhone(Long userId, UpdatePhoneParam updatePhoneParam);

    /**
     * 检查新的手机号是否可用,如可用,则给该新手机发送验证码
     * @param mobile 新手机号
     * @param countryCode 新手机号国家code
     * @return
     */
    boolean checkNewPhone(String mobile, String countryCode);

    /**
     * 修改用户的登录密码
     * @param userId 用户id
     * @param updateLoginParam 修改密码参数
     * @return 修改结果
     */
    boolean updateUserLoginPwd(Long userId, UpdateLoginParam updateLoginParam);

    /**
     * 修改用户的交易密码
     * @param userId 用户id
     * @param updateLoginParam 修改密码参数
     * @return 修改结果
     */
    boolean updateUserPayPwd(Long userId, UpdateLoginParam updateLoginParam);

    /**
     * 重新设置交易密码
     * @param userId 用户id
     * @param unsetPayPasswordParam 重置密码参数
     * @return 修改结果
     */
    boolean unsetPayPassword(Long userId, UnsetPayPasswordParam unsetPayPasswordParam);

    /**
     * 通过用户id 批量查询用户的基础信息
     * @param ids 用户id
     * @param userName 用户信息
     * @param mobile 用户手机号
     * @return
     */
    //List<UserDto> getBasicUsers(List<Long> ids);
    Map<Long,UserDto> getBasicUsers(List<Long> ids,String userName,String mobile);

    /**
     * 用户的注册
     * @param registerParam 注册的表单参数
     * @return 注册结果
     */
    Boolean register(RegisterParam registerParam);

    /**
     * 用户重置密码
     * @param unSetPasswordParam 重置密码的表单参数
     * @return 重置结果
     */
    boolean unsetLoginPwd(UnSetPasswordParam unSetPasswordParam);
}
