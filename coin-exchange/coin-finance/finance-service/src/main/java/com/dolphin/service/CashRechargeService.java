package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.CashRecharge;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dolphin.domain.CashRechargeAuditRecord;
import com.dolphin.model.CashParam;
import com.dolphin.vo.CashTradeVo;

public interface CashRechargeService extends IService<CashRecharge>{

    /**
     *
     * 分页条件查询充值记录
     * @param page 分页参数
     * @param coinId 币种的Id
     * @param userId 用户的Id
     * @param userName 用户的名称
     * @param mobile 用户的手机号
     * @param status 充值的状态
     * @param numMin 充值的最小金额
     * @param numMax 充值的最大金额
     * @param startTime 充值的开始时间
     * @param endTime 充值的截至时间
     * @return
     */
    Page<CashRecharge> findByPage(Page<CashRecharge> page,
                                  Long coinId, Long userId,
                                  String userName, String mobile,
                                  Byte status, String numMin,
                                  String numMax,
                                  String startTime, String endTime);

    /**
     * 现金的充值审核
     * @param userId 审核人Id
     * @param cashRechargeAuditRecord 审核的数据
     * @return 是否审核成功
     */
    boolean cashRechargeAudit(Long userId, CashRechargeAuditRecord cashRechargeAuditRecord);

    /**
     * 进行一个GCN/充值/购买
     * @param userId 用户的id
     * @param cashParam 现金参数
     * @return
     */
    CashTradeVo buy(Long userId, CashParam cashParam);
}
