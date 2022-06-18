package com.dolphin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.dolphin.domain.SysMenu;
import com.dolphin.feign.JwtToken;
import com.dolphin.feign.OAuth2FeignClient;
import com.dolphin.model.LoginResult;
import com.dolphin.service.SysLoginService;
import com.dolphin.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysLoginServiceImpl implements SysLoginService {

    @Autowired
    private OAuth2FeignClient oAuth2FeignClient;

    @Autowired
    private SysMenuService sysMenuService;

    @Value("${basic.token:Basic Y29pbi1hcGk6Y29pbi1zZWNyZXQ=}")
    private String basicToken;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public LoginResult login(String username, String password) {
        log.info("用户{}开始登录",username);
        //1获取登录 远程调用authorization-server 服务
        ResponseEntity<JwtToken> tokenResponseEntity = oAuth2FeignClient.getToken(
                "password",
                username,
                password,
                "admin_type",
                basicToken);
        if (tokenResponseEntity.getStatusCode()!= HttpStatus.OK){
            throw new ApiException(ApiErrorCode.FAILED);
        }
        JwtToken jwtToken = tokenResponseEntity.getBody();
        log.info("远程调用授权服务器成功，获取的token为{}", JSON.toJSONString(jwtToken,true));
        String token = jwtToken.getAccessToken();
        //2查询菜单数据
        Jwt jwt = JwtHelper.decode(token);
        //获取解析的Json数据
        String jwtJsonStr = jwt.getClaims();
        //Json数据转Json对象
        JSONObject jsonObject = JSON.parseObject(jwtJsonStr);
        Long userId = Long.valueOf(jsonObject.getString("user_name"));
        log.info("获取到的userId为:"+userId);
        List<SysMenu> menus = sysMenuService.getMenusByUserId(userId);
        //3查询权限数据
        JSONArray authoritiesJsonArray = jsonObject.getJSONArray("authorities");
        //组装权限数据
        List<SimpleGrantedAuthority> authorities = authoritiesJsonArray.stream()
                .map(authorityJson -> new SimpleGrantedAuthority(authorityJson.toString()))
                .collect(Collectors.toList());
        //4-1 将该token 存储在redis里面，配置我们的网关做jwt验证的操作
        redisTemplate.opsForValue().set(token,"", jwtToken.getExpiresIn(),TimeUnit.SECONDS);
        //4-2 返回给前端的数据，少一个bearer
        return new LoginResult(jwtToken.getTokenType()+" "+token,menus,authorities);
    }
}
