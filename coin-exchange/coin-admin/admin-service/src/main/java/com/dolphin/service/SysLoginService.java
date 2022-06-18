package com.dolphin.service;

import com.dolphin.model.LoginResult;

/**
 * 登录接口
 */
public interface SysLoginService {

    /**
     * 登录的实现
     * @param username 用户名
     * @param password 密码
     * @return 登录返回结果
     */
    LoginResult login(String username,String password);
}
