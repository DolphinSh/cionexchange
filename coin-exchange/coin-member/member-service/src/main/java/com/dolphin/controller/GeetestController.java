package com.dolphin.controller;


import com.dolphin.geetest.GeetestLib;
import com.dolphin.geetest.entity.GeetestLibResult;
import com.dolphin.geetest.enums.DigestmodEnum;
import com.dolphin.model.R;
import com.dolphin.task.CheckGeetestStatusTask;
import com.dolphin.util.IpUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/gt")
public class GeetestController {

    @Autowired
    private GeetestLib geetestLib;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/register")
    @ApiOperation(value = "获取极验的第一次数据包---")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid" ,value = "用户验证的一个凭证")
    })
    public R<Object> register(String uuid){
                /*
        必传参数
            digestmod 此版本sdk可支持md5、sha256、hmac-sha256，md5之外的算法需特殊配置的账号，联系极验客服
        自定义参数,可选择添加
            user_id 客户端用户的唯一标识，确定用户的唯一性；作用于提供进阶数据分析服务，可在register和validate接口传入，不传入也不影响验证服务的使用；若担心用户信息风险，可作预处理(如哈希处理)再提供到极验
            client_type 客户端类型，web：电脑上的浏览器；h5：手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生sdk植入app应用的方式；unknown：未知
            ip_address 客户端请求sdk服务器的ip地址
        */
        //response.setContentType("application/json;charset=UTF-8");
        //GeetestLib gtLib = new GeetestLib(PropertiesUtils.get("geetest.id"), PropertiesUtils.get("geetest.key"));
        GeetestLibResult result = null;
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 检测存入redis中的极验云状态标识
        //if (CheckGeetestStatusTask.checkGeetestStatusRedisFlag()) {
            DigestmodEnum digestmodEnum = DigestmodEnum.MD5;
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("digestmod", digestmodEnum.getName());
            paramMap.put("user_id", uuid);
            paramMap.put("client_type", "web");
            paramMap.put("ip_address", IpUtil.getIpAddr(servletRequestAttributes.getRequest()));
            //与服务器交互，得到结果
            result = geetestLib.register(digestmodEnum, paramMap);
        /*} else {
            result = geetestLib.localInit();
        }*/
        // 将结果状态写到session中，此处register接口存入session，后续validate接口会取出使用
        // 注意，此demo应用的session是单机模式，格外注意分布式环境下session的应用
        redisTemplate.opsForValue().set(GeetestLib.GEETEST_SERVER_STATUS_SESSION_KEY, result.getStatus(), 180, TimeUnit.SECONDS);
//        request.getSession().setAttribute( result.getStatus());
        redisTemplate.opsForValue().set(GeetestLib.GEETEST_SERVER_USER_KEY + ":" + uuid, uuid, 180, TimeUnit.SECONDS);
//        request.getSession().setAttribute("userId", userId);
        // 注意，不要更改返回的结构和值类型
        return R.ok(result.getData());
    }
}
