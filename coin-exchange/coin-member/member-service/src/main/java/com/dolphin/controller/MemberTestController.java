package com.dolphin.controller;

import com.dolphin.model.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "会员系统的测试")
public class MemberTestController {

    @GetMapping("/test")
    @ApiOperation(value = "会员系统的测试",authorizations = {@Authorization("Authorization")})
    public R<String> test(){
        return R.ok("会员系统搭建成功！");
    }
}
