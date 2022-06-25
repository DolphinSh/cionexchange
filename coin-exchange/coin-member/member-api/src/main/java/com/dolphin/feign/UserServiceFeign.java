package com.dolphin.feign;


import com.dolphin.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "member-service",configuration = OAuth2FeignConfig.class,path = "/users")
public interface UserServiceFeign {
    @GetMapping("/basic/users")
    List<UserDto> getBasicUsers(@RequestParam("ids") List<Long> ids);
}
