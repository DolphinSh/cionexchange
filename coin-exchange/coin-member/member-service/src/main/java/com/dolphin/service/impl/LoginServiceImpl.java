package com.dolphin.service.impl;

import com.alibaba.fastjson.JSON;
import com.dolphin.feign.JwtToken;
import com.dolphin.feign.OAuth2FeignClient;
import com.dolphin.geetest.GeetestLib;
import com.dolphin.geetest.entity.GeetestLibResult;
import com.dolphin.model.LoginForm;
import com.dolphin.model.LoginUser;
import com.dolphin.service.LoginService;
import com.dolphin.task.CheckGeetestStatusTask;
import com.dolphin.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private OAuth2FeignClient oAuth2FeignClient;

    @Value("${basic.token:Basic Y29pbi1hcGk6Y29pbi1zZWNyZXQ=}")
    private String basicToken;

    @Autowired
    private StringRedisTemplate strRedisTemplate;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private GeetestLib geetestLib;
    /**
     * 会员的登录
     *
     * @param loginForm 登录的表单参数
     * @return 登录的结果
     */
    @Override
    public LoginUser login(LoginForm loginForm) {
        log.info("用户{}开始登录",loginForm.getUsername());
        checkFromData(loginForm);
        LoginUser loginUser = null;
        ResponseEntity<JwtToken> tokenResponseEntity = oAuth2FeignClient.getToken(
                "password",
                loginForm.getUsername(),
                loginForm.getPassword(),
                "member_type",
                basicToken
        );
        if (tokenResponseEntity.getStatusCode() == HttpStatus.OK) {
            JwtToken jwtToken = tokenResponseEntity.getBody();
            log.info("远程调用成功.，结果为{}",jwtToken,true);
            loginUser = new LoginUser(
                    loginForm.getUsername(),
                    jwtToken.getExpiresIn(),
                    jwtToken.getTokenType()+" "+jwtToken.getAccessToken(),
                    jwtToken.getRefreshToken());
            //使用网关解决登出问题
            //token 是直接存入网关的
            redisTemplate.opsForValue().set(jwtToken.getAccessToken(),"",jwtToken.getExpiresIn(), TimeUnit.SECONDS);
        }
        //登录 -> 使用用户名和密码换token 远程调用 authorization-server
        return loginUser;
    }

    /**
     * 极速校验数据
     * @param loginForm
     */
    private void checkFromData(LoginForm loginForm) {
        loginForm.check(geetestLib,redisTemplate);
    }
}
