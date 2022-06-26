package com.dolphin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.alicloud.sms.ISmsService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.config.IdAutoConfiguration;
import com.dolphin.domain.Sms;
import com.dolphin.domain.UserAuthAuditRecord;
import com.dolphin.domain.UserAuthInfo;
import com.dolphin.dto.UserDto;
import com.dolphin.geetest.GeetestLib;
import com.dolphin.mappers.UserDtoMapper;
import com.dolphin.model.*;
import com.dolphin.service.UserAuthAuditRecordService;
import com.dolphin.service.UserAuthInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private Snowflake snowflake; //雪花算法

    @Autowired
    private UserAuthInfoService userAuthInfoService;

    @Autowired
    private SmsService smsService;

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
     * 用户进行高级认证
     *
     * @param userId  用户id
     * @param imgList 用户的图片地址
     * @return 提交高级认证结果
     */
    @Override
    @Transactional
    public boolean authUser(Long userId, List<String> imgList) {
        //对图片进行判断
        if (CollectionUtils.isEmpty(imgList)){
            throw new IllegalArgumentException("用户的身份认证信息为null");
        }
        User user = getById(userId);
        if (user == null){
            throw new IllegalArgumentException("请输入正确的userId");
        }
        long authCode = snowflake.nextId(); //使用时间戳(有重复) --> 雪花算法
        List<UserAuthInfo> userAuthInfoList = new ArrayList<>(imgList.size());
        for (int i = 0; i < imgList.size(); i++) {
            String s = imgList.get(i);
            UserAuthInfo userAuthInfo = new UserAuthInfo();
            userAuthInfo.setImageUrl(imgList.get(i));
            userAuthInfo.setUserId(userId);
            userAuthInfo.setSerialno(i + 1);  // 设置序号 ,1 正面  2 反面 3 手持
            userAuthInfo.setAuthCode(authCode); // 是一组身份信息的标识 3 个图片为一组
            userAuthInfoList.add(userAuthInfo);
        }
        userAuthInfoService.saveBatch(userAuthInfoList);
        user.setReviewsStatus(0); //更新状态为等待审核
        boolean isOk = updateById(user);//更新用户的状态
        return isOk;
    }

    /**
     * 修改用户的手机号
     *
     * @param userId           用户id
     * @param updatePhoneParam 修改手机的参数
     * @return
     */
    @Override
    public boolean updatePhone(Long userId, UpdatePhoneParam updatePhoneParam) {
        //1 使用userId 查询用户
        User user = getById(userId);
        //2 验证旧手机
        String oldMobile = user.getMobile(); //获取旧的手机号  ----> 验证旧手机的验证码
        String oldMobileMsgCode = stringRedisTemplate.opsForValue().get("SMS:VERIFY_OLD_PHONE:" + oldMobile);
        if (!updatePhoneParam.getOldValidateCode().equals(oldMobileMsgCode)){
            throw new IllegalArgumentException("旧的手机号的验证码错误！");
        }
        //4 验证新手机
        String newPhoneMsgCode = stringRedisTemplate.opsForValue().get("SMS:CHANGE_PHONE_VERIFY:" + updatePhoneParam.getNewMobilePhone());
        if (!updatePhoneParam.getValidateCode().equals(newPhoneMsgCode)){
            throw new IllegalArgumentException("新手机的验证码错误！");
        }
        //5 修改手机号更新到数据库
        user.setMobile(updatePhoneParam.getNewMobilePhone());
        return updateById(user);
    }

    /**
     * 检查新的手机号是否可用,如可用,则给该新手机发送验证码
     *
     * @param mobile      新手机号
     * @param countryCode 新手机号国家code
     * @return
     */
    @Override
    public boolean checkNewPhone(String mobile, String countryCode) {
        //1 新手机号没有旧的用户使用 确保唯一性
        int count = count(new LambdaQueryWrapper<User>().eq(!StringUtils.isEmpty(mobile), User::getMobile, mobile));
        if (count > 0){
            throw new IllegalArgumentException("该手机号已经被占用!");
        }
        Sms sms = new Sms();
        sms.setMobile(mobile);
        sms.setCountryCode(countryCode);
        sms.setTemplateCode("CHANGE_PHONE_VERIFY"); //模板代码  -- > 校验手机号
        return smsService.sendSms(sms);
    }

    /**
     * 修改用户的登录密码
     *
     * @param userId           用户id
     * @param updateLoginParam 修改密码参数
     * @return
     */
    @Override
    public boolean updateUserLoginPwd(Long userId, UpdateLoginParam updateLoginParam) {
        // 1 用户id校验
        User user = getById(userId);
        if (user == null){
            throw new IllegalArgumentException("用户的id错误");
        }

        //2 校验之前的密码 数据库的密码是我们加密后的密码。
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(updateLoginParam.getOldpassword(), user.getPassword());
        if (!matches){
            throw new IllegalArgumentException("用户的原始密码输入错误");
        }
        //3 进行验证码校验
        String validateCode = updateLoginParam.getValidateCode();
        String phoneValidateCode = stringRedisTemplate.opsForValue().get("SMS:CHANGE_LOGIN_PWD_VERIFY:"+user.getMobile());
        if (!validateCode.equals(phoneValidateCode)) {
            throw new IllegalArgumentException("手机验证码错误");
        }
        //4 结果保存
        user.setPaypassword(bCryptPasswordEncoder.encode(updateLoginParam.getNewpassword()));//修改为加密后的密码
        return updateById(user);
    }

    /**
     * 修改用户的交易密码
     *
     * @param userId           用户id
     * @param updateLoginParam 修改密码参数
     * @return 修改结果
     */
    @Override
    public boolean updateUserPayPwd(Long userId, UpdateLoginParam updateLoginParam) {
        // 1 查询之前的用户
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户的Id错误");
        }
        // 2 校验之前的密码 数据库的密码都是我们加密后的密码.-->
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        boolean matches = bCryptPasswordEncoder.matches(updateLoginParam.getOldpassword(), user.getPaypassword());
        if (!matches) {
            throw new IllegalArgumentException("用户的原始密码输入错误");
        }
        // 3 校验手机的验证码
        String validateCode = updateLoginParam.getValidateCode();
        String phoneValidateCode = stringRedisTemplate.opsForValue().get("SMS:CHANGE_PAY_PWD_VERIFY:" + user.getMobile());//"SMS:CHANGE_LOGIN_PWD_VERIFY:111111"
        if (!validateCode.equals(phoneValidateCode)) {
            throw new IllegalArgumentException("手机验证码错误");
        }
        //4 结果保存
        user.setPaypassword(bCryptPasswordEncoder.encode(updateLoginParam.getNewpassword())); // 修改为加密后的密码
        return updateById(user);
    }

    /**
     * 重新设置交易密码
     *
     * @param userId                用户id
     * @param unsetPayPasswordParam 重置密码参数
     * @return 修改结果
     */
    @Override
    public boolean unsetPayPassword(Long userId, UnsetPayPasswordParam unsetPayPasswordParam) {
        User user = getById(userId);
        if (user == null){
            throw new IllegalArgumentException("用户的Id错误");
        }
        String validateCode = unsetPayPasswordParam.getValidateCode();
        String phoneValidate = stringRedisTemplate.opsForValue().get("SMS:FORGOT_PAY_PWD_VERIFY:" + user.getMobile());
        if (!validateCode.equals(phoneValidate)) {
            throw new IllegalArgumentException("用户的验证码错误");
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPaypassword(bCryptPasswordEncoder.encode(unsetPayPasswordParam.getPayPassword()));

        return updateById(user);
    }

    /**
     * 通过用户id 批量查询用户的基础信息
     *
     * @param ids      用户id
     * @param userName 用户信息
     * @param mobile   用户手机号
     * @return
     */
    @Override
    public Map<Long, UserDto> getBasicUsers(List<Long> ids, String userName, String mobile) {
        if (CollectionUtils.isEmpty(ids)&&StringUtils.isEmpty(userName)&&StringUtils.isEmpty(mobile)){
            return Collections.emptyMap();
        }
        List<User> list = list(new LambdaQueryWrapper<User>()
                .in(!CollectionUtils.isEmpty(ids),User::getId, ids)
                .like(!StringUtils.isEmpty(userName),User::getUsername,userName)
                .like(!StringUtils.isEmpty(mobile),User::getMobile,mobile)
        );
        if (CollectionUtils.isEmpty(list)){
            return Collections.emptyMap();
        }
        //将user -> userDto
        List<UserDto> userDtos = UserDtoMapper.INSTANCE.convert2Dto(list);
        Map<Long, UserDto> userDtoMappings = userDtos.stream().collect(Collectors.toMap(UserDto::getId, userDto -> userDto));
        return userDtoMappings;
    }

    /**
     * 用户的注册
     *
     * @param registerParam 注册的表单参数
     * @return 注册结果
     */
    @Override
    public Boolean register(RegisterParam registerParam) {
        log.info("用户开始注册{}", JSON.toJSONString(registerParam, true));
        String mobile = registerParam.getMobile();
        String email = registerParam.getEmail();
        // 1 简单的校验
        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(mobile)) {
            throw new IllegalArgumentException("手机号或邮箱不能同时为空");
        }
        // 2 查询校验
        int count = count(new LambdaQueryWrapper<User>()
                .eq(!StringUtils.isEmpty(email), User::getEmail, email)
                .eq(!StringUtils.isEmpty(mobile), User::getMobile, mobile)
        );
        if (count > 0) {
            throw new IllegalArgumentException("手机号或邮箱已经被注册");
        }
        // 3 极验校验
        registerParam.check(geetestLib, redisTemplate); // 进行极验的校验
        User user = getUser(registerParam);
        return save(user);
    }

    /**
     * 用户重置密码
     *
     * @param unSetPasswordParam 重置密码的表单参数
     * @return 重置结果
     */
    @Override
    public boolean unsetLoginPwd(UnSetPasswordParam unSetPasswordParam) {
        log.info("开始重置密码{}", JSON.toJSONString(unSetPasswordParam, true));
        // 1 极验校验
        unSetPasswordParam.check(geetestLib, redisTemplate);
        // 2 手机号码校验
        String phoneValidateCode = stringRedisTemplate.opsForValue().get("SMS:FORGOT_VERIFY:" + unSetPasswordParam.getMobile());
        if (!unSetPasswordParam.getValidateCode().equals(phoneValidateCode)) {
            throw new IllegalArgumentException("手机验证码错误");
        }
        // 3 数据库用户的校验

        String mobile = unSetPasswordParam.getMobile();
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getMobile, mobile));
        if (user == null) {
            throw new IllegalArgumentException("该用户不存在");
        }
        String encode = new BCryptPasswordEncoder().encode(unSetPasswordParam.getPassword());
        user.setPassword(encode);
        return updateById(user);
    }

    /**
     * 构建一个新的用户
     * @param registerParam
     * @return
     */
    private User getUser(RegisterParam registerParam) {
        User user = new User();
        user.setCountryCode(registerParam.getCountryCode());
        user.setEmail(registerParam.getEmail());
        user.setMobile(registerParam.getMobile());
        String encodePwd = new BCryptPasswordEncoder().encode(registerParam.getPassword());
        user.setPassword(encodePwd);
        user.setPaypassSetting(false);
        user.setStatus((byte) 1);
        user.setType((byte) 1);
        user.setAuthStatus((byte) 0);
        user.setLogins(0);
        user.setInviteCode(RandomUtil.randomString(6)); // 用户的邀请码
        if (!StringUtils.isEmpty(registerParam.getInvitionCode())) {
            User userPre = getOne(new LambdaQueryWrapper<User>().eq(User::getInviteCode, registerParam.getInvitionCode()));
            if (userPre != null) {
                user.setDirectInviteid(String.valueOf(userPre.getId())); // 邀请人的id , 需要查询
                user.setInviteRelation(String.valueOf(userPre.getId())); // 邀请人的id , 需要查询
            }
        }
        return user;
    }


    /**
     * 极验校验
     * @param userAuthForm
     */
    private void checkForm(UserAuthForm userAuthForm) {
        userAuthForm.check(geetestLib,redisTemplate);
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
