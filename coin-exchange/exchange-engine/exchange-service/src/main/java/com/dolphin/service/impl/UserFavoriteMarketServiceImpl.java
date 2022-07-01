package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.UserFavoriteMarketMapper;
import com.dolphin.domain.UserFavoriteMarket;
import com.dolphin.service.UserFavoriteMarketService;
@Service
public class UserFavoriteMarketServiceImpl extends ServiceImpl<UserFavoriteMarketMapper, UserFavoriteMarket> implements UserFavoriteMarketService{

    /**
     * 取消某个收藏交易对
     *
     * @param marketId
     * @param userId
     * @return
     */
    @Override
    public boolean deleteUserFavoriteMarket(Long marketId, Long userId) {
        return remove(new LambdaQueryWrapper<UserFavoriteMarket>()
                .eq(marketId != null,UserFavoriteMarket::getMarketId,marketId)
                .eq(userId != null ,UserFavoriteMarket::getUserId,userId)
        );
    }
}
