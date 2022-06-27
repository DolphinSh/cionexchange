package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.AdminBank;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dolphin.dto.AdminBankDto;

import java.util.List;

public interface AdminBankService extends IService<AdminBank>{

    /**
     * 分页条件查询公司银行卡
     * @param page
     * @param bankCard
     * @return
     */
    Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard);

    /**
     * 查询所有的银行卡的信息
     * @return
     */
    List<AdminBankDto> getAllAdminBanks();
}
