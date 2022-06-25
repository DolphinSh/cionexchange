package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.User;
import com.dolphin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.UserBank;
import com.dolphin.mapper.UserBankMapper;
import com.dolphin.service.UserBankService;

@Service
public class UserBankServiceImpl extends ServiceImpl<UserBankMapper, UserBank> implements UserBankService {

    @Autowired
    private UserService userService;

    /**
     * 根据用户id分页查询用户的银行卡
     *
     * @param page
     * @param usrId
     * @return
     */
    @Override
    public Page<UserBank> findByPage(Page<UserBank> page, Long usrId) {
        return page(page, new LambdaQueryWrapper<UserBank>()
                .eq(usrId != null, UserBank::getUserId, usrId)
        );
    }

    /**
     * 通过用户的ID查询用户的银行卡
     *
     * @param userId 用户的ID
     * @return
     */
    @Override
    public UserBank getCurrentUserBank(Long userId) {
        return getOne(new LambdaQueryWrapper<UserBank>()
                .eq(UserBank::getUserId, userId)
                .eq(UserBank::getStatus, 1)
        );
    }

    /**
     * 绑定银行卡
     *
     * @param userId
     * @param userBank
     * @return
     */
    @Override
    public boolean bindBank(Long userId, UserBank userBank) {
        String payPassword = userBank.getPayPassword();
        User user = userService.getById(userId);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(payPassword, user.getPaypassword())) {
            throw new IllegalArgumentException("用户的支付密码错误");
        }
        Long id = userBank.getId();//暂定为0 添加 15 是修改
        if (id != 0) {
            UserBank userBankDB = getById(id);
            if (userBankDB == null) {
                throw new IllegalArgumentException("用户的银行卡的ID输入错误");
            }
            return updateById(userBank);// 修改值
        }
        // 若银行卡的id为null ,则需要新建一个
        userBank.setUserId(userId);
        userBank.setStatus((byte)1);
        return save(userBank);
    }
}
