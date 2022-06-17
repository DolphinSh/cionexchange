package com.dolphin.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.dolphin.model.WebLog;
import com.dolphin.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    /**
     * 通过username查询webLog
     * @param username
     * @return
     */
    @Cached(name = "com.dolphin.service.impl.TestServiceImpl",key = "#username",cacheType = CacheType.BOTH)
    @Override
    public WebLog get(String username) {
        WebLog webLog = new WebLog();
        webLog.setUsername(username);
        webLog.setResult("ok");
        return webLog;
    }
}
