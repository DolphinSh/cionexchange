package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserAddressService extends IService<UserAddress>{

    /**
     * 查阅用户的钱包地址
     * @param page 分页条件
     * @param userId 用户id
     * @return
     */
    Page<UserAddress> findByPage(Page<UserAddress> page, Long userId);
}
