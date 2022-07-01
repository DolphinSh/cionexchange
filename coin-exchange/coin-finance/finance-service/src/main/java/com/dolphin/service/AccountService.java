package com.dolphin.service;

import com.dolphin.domain.Account;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dolphin.vo.UserTotalAccountVo;

import java.math.BigDecimal;

public interface AccountService extends IService<Account>{

    /**
     * 用户资金的划转
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

    /**
     * 给用户扣减钱
     * @param adminId 操作的人的id
     * @param userId 用户的id
     * @param coinId 币种id
     * @param orderNum 订单的编号
     * @param num 扣减的余额
     * @param fee 费用
     * @param remark 备注
     * @param businessType 业务类型
     * @param direction 方向
     * @return
     */
    Boolean decreaseAccountAmount(Long adminId, Long userId, Long coinId, Long orderNum, BigDecimal num, BigDecimal fee, String remark, String businessType, byte direction);

    /**
     * 获取当前用户的货币的资产情况
     * @param userId 用户的id
     * @param coinName 货币的名称
     * @return
     */
    Account findByUserAndCoin(Long userId, String coinName);

    /**
     * 暂时锁定用户的资产
     * @param userId 用户的id
     * @param coinId 币种的id
     * @param mum 锁定的金额
     * @param type 资金流水的类型
     * @param orderId 订单的Id
     * @param fee 本次操作的手续费
     */
    void lockUserAmount(Long userId, Long coinId, BigDecimal mum, String type, Long orderId, BigDecimal fee);

    /**
     * 计算用户的总资产
     * @param userId
     * @return
     */
    UserTotalAccountVo getUserTotalAccount(Long userId);
}
