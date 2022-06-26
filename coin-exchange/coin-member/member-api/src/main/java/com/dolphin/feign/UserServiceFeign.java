package com.dolphin.feign;


import com.dolphin.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "member-service",configuration = OAuth2FeignConfig.class,path = "/users")
public interface UserServiceFeign {
    //@GetMapping("/basic/users")
    //List<UserDto> getBasicUsers(@RequestParam("ids") List<Long> ids);

    /**
     *      * @param ids
     *      * @return Map<Long,UserDto> Long 用户id
     *      * UserDto 用户的基础信息
     * @param ids
     * @param userName
     * @param mobile
     * @return
     */
    @GetMapping("/basic/users")
    Map<Long,UserDto> getBasicUsers(@RequestParam(value = "ids",required = false) List<Long> ids,
                                    @RequestParam(value = "userName",required = false) String userName,
                                    @RequestParam(value = "mobile",required = false) String mobile
                                    );


}
