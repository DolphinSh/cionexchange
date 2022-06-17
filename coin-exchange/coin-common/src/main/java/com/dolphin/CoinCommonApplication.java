package com.dolphin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient //开启服务的注册与发现
public class CoinCommonApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoinCommonApplication.class, args);
    }
}
