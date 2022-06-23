package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
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
}
