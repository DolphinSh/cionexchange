package com.dolphin.service;

import com.dolphin.domain.Account;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

public interface AccountService extends IService<Account>{

    /**
     *
     * @param adminId 管理员id
     * @param userId 用户id
     * @param coinId 币种id
     * @param orderNum 订单编号
     * @param num 数量
     * @param fee 手续费
     * @param remark 备注
     * @param businessType 交易类型
     * @param direction 交易方向 进 出 账
     * @return
     */
    Boolean transferAccountAmount(Long adminId, Long userId, Long coinId, Long orderNum, BigDecimal num, BigDecimal fee, String remark, String businessType, Byte direction);
}
