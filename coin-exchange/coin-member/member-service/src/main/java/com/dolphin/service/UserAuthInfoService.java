package com.dolphin.service;

import com.dolphin.domain.UserAuthInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserAuthInfoService extends IService<UserAuthInfo>{

    /**
     * 通过认证的code来查询用户的认证的详情
     * @param authCode 认证的唯一code
     * @return
     */
    List<UserAuthInfo> getUserAuthInfoByCode(Long authCode);

    /**
     * 没有被认证过，通过用户的id来进行认证
     * @param id
     * @return
     */
    List<UserAuthInfo> getUserAuthInfoByUserId(Long id);
}
