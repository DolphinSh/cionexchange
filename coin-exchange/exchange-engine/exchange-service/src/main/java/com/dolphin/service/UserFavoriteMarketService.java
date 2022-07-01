package com.dolphin.service;

import com.dolphin.domain.UserFavoriteMarket;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserFavoriteMarketService extends IService<UserFavoriteMarket>{

    /**
     * 取消某个收藏交易对
     * @param marketId
     * @param userId
     * @return
     */
    boolean deleteUserFavoriteMarket(Long marketId, Long userId);
}
