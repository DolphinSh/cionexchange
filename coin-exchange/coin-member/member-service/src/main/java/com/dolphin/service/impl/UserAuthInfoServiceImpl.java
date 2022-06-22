package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.UserAuthInfoMapper;
import com.dolphin.domain.UserAuthInfo;
import com.dolphin.service.UserAuthInfoService;

@Service
public class UserAuthInfoServiceImpl extends ServiceImpl<UserAuthInfoMapper, UserAuthInfo> implements UserAuthInfoService {

    /**
     * 通过认证的code来查询用户的认证的详情
     *
     * @param authCode 认证的唯一code
     * @return
     */
    @Override
    public List<UserAuthInfo> getUserAuthInfoByCode(Long authCode) {
        return list(new LambdaQueryWrapper<UserAuthInfo>()
                .eq(authCode != null, UserAuthInfo::getAuthCode, authCode)
        );
    }

    /**
     * 没有被认证过，通过用户的id来进行认证
     *
     * @param id
     * @return
     */
    @Override
    public List<UserAuthInfo> getUserAuthInfoByUserId(Long id) {
        List<UserAuthInfo> userAuthInfoList = list(new LambdaQueryWrapper<UserAuthInfo>()
                .eq(id != null, UserAuthInfo::getUserId, id)
        );
        //空处理
        return userAuthInfoList == null? Collections.emptyList():userAuthInfoList;
    }
}
