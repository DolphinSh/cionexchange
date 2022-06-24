package com.dolphin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identify")
@Data
public class IdProperties {

    private String url; //身份认证的地址 http(s)://idcert.market.alicloudapi.com/idcard

    /***
     * 你购买的appKey
     */
    private String appKey ;

    /***
     * 你购买的appSecret
     */
    private String appSecret ;

    /***
     * 你购买的appCode
     */
    private String appCode ;
}
