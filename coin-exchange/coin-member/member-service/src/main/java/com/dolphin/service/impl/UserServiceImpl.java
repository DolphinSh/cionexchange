package com.dolphin.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.config.IdAutoConfiguration;
import com.dolphin.domain.UserAuthAuditRecord;
import com.dolphin.geetest.GeetestLib;
import com.dolphin.model.UserAuthForm;
import com.dolphin.service.UserAuthAuditRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.UserMapper;
import com.dolphin.domain.User;
import com.dolphin.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserAuthAuditRecordService userAuthAuditRecordService;

    @Autowired
    private GeetestLib geetestLib;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 条件分页查询查询会员的列表
     *
     * @param page
     * @param mobile   会员的手机号
     * @param userId   会员的Id
     * @param userName 会员的名称
     * @param realName 会员的真实名称
     * @param status   会员的状态
     * @param reviewsStatus 会员的审核状态
     * @return
     */
    @Override
    public Page<User> findByPage(Page<User> page, String mobile, Long userId, String userName, String realName, Integer status,Integer reviewsStatus) {
        return page(page,
                new LambdaQueryWrapper<User>()
                        .like(!StringUtils.isEmpty(mobile), User::getMobile, mobile)
                        .like(!StringUtils.isEmpty(userName), User::getUsername, userName)
                        .like(!StringUtils.isEmpty(realName), User::getRealName, realName)
                        .eq(userId != null, User::getId, userId)
                        .eq(status != null, User::getStatus, status)
                        .eq(reviewsStatus != null,User::getReviewsStatus,reviewsStatus)
        );
    }

    /**
     * 查询该用户邀请的用户列表
     *
     * @param page   分页参数
     * @param userId 用户id
     * @return
     */
    @Override
    public Page<User> findDirectInvitePage(Page<User> page, Long userId) {
        return page(page, new LambdaQueryWrapper<User>()
                .eq(userId != null, User::getDirectInviteid, userId)
        );
    }

    /**
     * 修改用户的审核状态
     *
     * @param id
     * @param authStatus
     * @param authCode
     * @param remark
     */
    @Override
    @Transactional
    public void updateUserAuthStatus(Long id, Byte authStatus, Long authCode, String remark) {
        log.info("开始修改用户的审核状态,当前用户{},用户的审核状态{},图片的唯一code{}", id, authStatus, authCode);
        User user = getById(id);
        if (user != null){
            user.setReviewsStatus(authStatus.intValue());//审核的状态
            updateById(user); //修改用户的状态
        }
        UserAuthAuditRecord userAuthAuditRecord = new UserAuthAuditRecord();
        userAuthAuditRecord.setUserId(id);
        userAuthAuditRecord.setStatus(authStatus);
        userAuthAuditRecord.setAuthCode(authCode);

        String usrStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        userAuthAuditRecord.setAuditUserId(Long.valueOf(usrStr)); // 审核人的ID
        userAuthAuditRecord.setAuditUserName("---------------------------");// 审核人的名称 --> 远程调用admin-service ,没有事务
        userAuthAuditRecord.setRemark(remark);
        userAuthAuditRecordService.save(userAuthAuditRecord);

    }

    /**
     * 用户实名认证
     *
     * @param id           用户实名认证id
     * @param userAuthForm 用户表单
     * @return
     */
    @Override
    public boolean identifyVerfiy(Long id, UserAuthForm userAuthForm) {
        User user = getById(id);
        Assert.notNull(user,"认证的用户不存在");
        Byte authStatus = user.getAuthStatus();
        //0 是未认证状态
        if (authStatus.equals("0")){
            throw new IllegalArgumentException("该用户已经认证成功了！");
        }
        //执行认证检查，极验
        checkForm(userAuthForm);
        //调用阿里云接口进行实名认证
        boolean check = IdAutoConfiguration.check(userAuthForm.getRealName(), userAuthForm.getIdCard());
        if (!check){
            throw new IllegalArgumentException("该用户信息错误，请检查");
        }
        user.setAuthtime(DateUtil.date());
        user.setAuthStatus((byte)1);
        user.setRealName(userAuthForm.getRealName());
        user.setIdCard(userAuthForm.getIdCard());
        user.setIdCardType(userAuthForm.getIdCardType());
        //执行更新操作
        boolean updateStatus = updateById(user);
        return updateStatus;
    }

    /**
     * 极验校验
     * @param userAuthForm
     */
    private void checkForm(UserAuthForm userAuthForm) {
        userAuthForm.check(userAuthForm,geetestLib,redisTemplate);
    }

    @Override
    public User getById(Serializable id) {
        User user = super.getById(id);
        if (user == null){
            throw new IllegalArgumentException("请输入正确的用户id");
        }
        Byte seniorAuthStatus = null; //用户认证高级认证状态
        String seniorAuthDesc = "";//用户的高级认证未通过，原因
        Integer reviewsStatus = user.getReviewsStatus(); //用户被审核的状态 1 通过，2 拒绝 ，0 待审核
        if (reviewsStatus == null){
            seniorAuthStatus = 3;
            seniorAuthDesc = "资料未填写";
        }else {
            switch (reviewsStatus){
                case 1:
                    seniorAuthStatus = 1;
                    seniorAuthDesc = "审核通过";
                    break;
                case 2:
                    seniorAuthStatus = 2;
                    //查询被拒绝的原因 -> 审核记录里面的
                    List<UserAuthAuditRecord> userAuthAuditRecordList = userAuthAuditRecordService.getUserAuthAuditRecordList((Long) id);
                    if (!CollectionUtils.isEmpty(userAuthAuditRecordList)){
                        UserAuthAuditRecord userAuthAuditRecord = userAuthAuditRecordList.get(0);
                        seniorAuthDesc = userAuthAuditRecord.getRemark();
                    }
                    break;
                case 0:
                    seniorAuthStatus = 0;
                    seniorAuthDesc = "等待审核";
                    break;
            }
        }
        user.setSeniorAuthStatus(seniorAuthStatus);
        user.setSeniorAuthDesc(seniorAuthDesc);
        return user;
    }
}
