package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.UserAddress;
import com.dolphin.mapper.UserAddressMapper;
import com.dolphin.service.UserAddressService;
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService{

    /**
     * 查阅用户的钱包地址
     *
     * @param page   分页条件
     * @param userId 用户id
     * @return
     */
    @Override
    public Page<UserAddress> findByPage(Page<UserAddress> page, Long userId) {
        return page(page,new LambdaQueryWrapper<UserAddress>()
                .eq(userId != null,UserAddress::getUserId,userId)
        );
    }

    /**
     * 使用用户的Id 和币种的Id 查询用户的充币地址
     *
     * @param userId
     * @param coinId
     * @return
     */
    @Override
    public UserAddress getUserAddressByUserIdAndCoinId(String userId, Long coinId) {
        return  getOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId,userId)
                .eq(UserAddress::getCoinId,coinId)
        );
    }
}
