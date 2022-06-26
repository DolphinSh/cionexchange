package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.AccountDetail;
import com.baomidou.mybatisplus.extension.service.IService;
public interface AccountDetailService extends IService<AccountDetail>{

    /**
     * 分页条件查询充值记录
     * @param page 分页参数
     * @param coinId 币种的Id
     * @param accountId 账号的Id
     * @param userId 用户的id
     * @param userName 用户的名称
     * @param mobile 用户的手机
     * @param amountStart 金额最小值
     * @param amountEnd 金额的最大值
     * @param startTime 起始时间
     * @param endTime 截至时间
     * @return
     */
    Page<AccountDetail> findByPage(Page<AccountDetail> page, Long coinId, Long accountId, Long userId, String userName, String mobile, String amountStart, String amountEnd, String startTime, String endTime);
}
