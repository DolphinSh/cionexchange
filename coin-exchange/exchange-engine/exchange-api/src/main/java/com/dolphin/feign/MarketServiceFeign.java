package com.dolphin.feign;

import com.dolphin.dto.MarketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchange-service",contextId = "marketServiceFeign",configuration = OAuth2FeignConfig.class,path = "/markets")
public interface MarketServiceFeign {
    /**
     * 使用报价货币 以及 出售的货币的iD
     *
     * @param buyCoinId
     * @return
     */
    @GetMapping("/getMarket")
    MarketDto findByCoinId(@RequestParam("buyCoinId") Long buyCoinId, @RequestParam("sellCoinId") Long sellCoinId);

    @GetMapping("/getMarket/symbol")
    MarketDto findBySymbol(@RequestParam("symbol") String symbol);
}
