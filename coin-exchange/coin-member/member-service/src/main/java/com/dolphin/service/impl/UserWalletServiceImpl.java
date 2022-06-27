package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.User;
import com.dolphin.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.UserWallet;
import com.dolphin.mapper.UserWalletMapper;
import com.dolphin.service.UserWalletService;

@Service
public class UserWalletServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet> implements UserWalletService {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户的提币地址
     *
     * @param page   分页参数
     * @param userId 用户id
     * @return
     */
    @Override
    public Page<UserWallet> findByPage(Page<UserWallet> page, Long userId) {
        return page(page, new LambdaQueryWrapper<UserWallet>()
                .eq(userId != null, UserWallet::getUserId, userId)
        );
    }

    /**
     * 查询用户某种币的提现地址
     *
     * @param userId 用户id
     * @param coinId 币种id
     * @return
     */
    @Override
    public List<UserWallet> findUserWallets(Long userId, Long coinId) {
        return list(new LambdaQueryWrapper<UserWallet>()
                .eq(userId != null, UserWallet::getUserId, userId)
                .eq(coinId != null, UserWallet::getCoinId, coinId)
        );
    }

    /**
     * 删除某个用户的提现地址
     *
     * @param addressId
     * @param payPassword
     * @return
     */
    @Override
    public boolean deleteUserWallet(Long addressId, String payPassword) {
        UserWallet userWallet = getById(addressId);
        if (userWallet == null){
            throw new IllegalArgumentException("提现地址错误！");
        }
        Long userId = userWallet.getUserId();
        User user = userService.getById(userId);
        if (user == null){
            throw new IllegalArgumentException("该用户不存在！");
        }
        String payDBPassword = user.getPaypassword();
        if (StringUtils.isEmpty(payDBPassword) || !(new BCryptPasswordEncoder().matches(payPassword,payDBPassword))){
            throw new IllegalArgumentException("交易密码错误！");
        }
        return super.removeById(addressId);
    }

    @Override
    public boolean save(UserWallet entity) {
        Long userId = entity.getUserId();
        User user = userService.getById(userId);
        if (user == null){
            throw new IllegalArgumentException("该用户不存在！");
        }
        String paypassword = user.getPaypassword();
        if (StringUtils.isEmpty(paypassword) || !(new BCryptPasswordEncoder().matches(entity.getPayPassword(),paypassword))){
            throw new IllegalArgumentException("交易密码错误！");
        }
        return super.save(entity);
    }
}
