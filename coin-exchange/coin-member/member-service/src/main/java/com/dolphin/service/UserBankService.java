package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.UserBank;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserBankService extends IService<UserBank>{

    /**
     * 根据用户id分页查询用户的银行卡
     * @param page
     * @param usrId
     * @return
     */
    Page<UserBank> findByPage(Page<UserBank> page, Long usrId);
}
