package com.dolphin.match;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.match")
public class MatchEngineProperties {
    /**交易的信息对*/
    private Map<String,CoinScale> symbols;

    @Data
    public static  class  CoinScale {
        /**
         * 交易币种的精度
         */
        private int coinScale;

        /**
         * 基币的精度
         */
        private int baseCoinScale;
    }
}
