package com.dolphin.feign;

import com.dolphin.dto.UserBankDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 若FeignClient 里面的name相同时，spring创建对象就会报错。认为这两个对象是一致的
 */
@FeignClient(name = "member-service", contextId = "UserBankServiceFeign",configuration = OAuth2FeignConfig.class,path = "/userBanks")
public interface UserBankServiceFeign {

    @GetMapping("/{userId}/info")
    UserBankDto getUserBankInfo(@PathVariable Long userId);
}
