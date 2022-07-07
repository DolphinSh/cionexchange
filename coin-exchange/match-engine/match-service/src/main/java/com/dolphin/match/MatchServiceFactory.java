package com.dolphin.match;

import java.util.HashMap;
import java.util.Map;

/**
 * 交易策略工厂 StrategyFactory
 */
public class MatchServiceFactory {
    private static Map<MatchStrategy,MatchService> matchServiceMap = new HashMap<>();

    /**
     * 给策略工厂里面添加一个交易的实现类型
     * @param matchStrategy
     * @param matchService
     */
    public static  void addMatchService(MatchStrategy matchStrategy,MatchService matchService){
        matchServiceMap.put(matchStrategy ,matchService ) ;
    }


    /**
     * 使用策略的名称获取具体的实现类
     * @param matchStrategy
     * @return
     */
    public static MatchService getMatchService(MatchStrategy matchStrategy){
        return matchServiceMap.get(matchStrategy) ;
    }
}
