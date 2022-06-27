package com.dolphin.controller;

import com.dolphin.model.R;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "交易系统的测试")
public class TestController {

    @GetMapping("/test")
    public R<String> test(){
        return R.ok("测试成功");
    }
}
