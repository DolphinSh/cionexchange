package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.UserBank;
import com.dolphin.mapper.UserBankMapper;
import com.dolphin.service.UserBankService;
@Service
public class UserBankServiceImpl extends ServiceImpl<UserBankMapper, UserBank> implements UserBankService{

    /**
     * 根据用户id分页查询用户的银行卡
     *
     * @param page
     * @param usrId
     * @return
     */
    @Override
    public Page<UserBank> findByPage(Page<UserBank> page, Long usrId) {
        return page(page,new LambdaQueryWrapper<UserBank>()
                .eq(usrId!=null,UserBank::getUserId,usrId)
        );
    }
}
