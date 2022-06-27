package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.UserWallet;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserWalletService extends IService<UserWallet>{

    /**
     * 分页查询用户的提币地址
     * @param page 分页参数
     * @param userId 用户id
     * @return
     */
    Page<UserWallet> findByPage(Page<UserWallet> page, Long userId);

    /**
     * 查询用户某种币的提现地址
     * @param userId 用户id
     * @param coinId 币种id
     * @return
     */
    List<UserWallet> findUserWallets(Long userId, Long coinId);

    /**
     * 删除某个用户的提现地址
     * @param addressId
     * @param payPassword
     * @return
     */
    boolean deleteUserWallet(Long addressId, String payPassword);
}
