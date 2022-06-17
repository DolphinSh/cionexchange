package com.dolphin.service;

import com.dolphin.model.WebLog;

public interface TestService {
    /**
     * 通过username 查询webLog
     * @param username
     * @return
     */
    WebLog get(String username);
}
