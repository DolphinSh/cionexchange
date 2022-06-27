package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.CashWithdrawAuditRecord;
import com.dolphin.domain.CashWithdrawals;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CashWithdrawalsService extends IService<CashWithdrawals> {

    /**
     * 提现记录的查询
     *
     * @param page      分页数据
     * @param userId    用户的id
     * @param userName  用户的名称
     * @param mobile    用户的手机号
     * @param status    提现的状态
     * @param numMin    提现的最小金额
     * @param numMax    提现的最大金额
     * @param startTime 提现的开始时间
     * @param endTime   提现的截至时间
     * @return
     */
    Page<CashWithdrawals> findByPage(Page<CashWithdrawals> page, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime);

    /**
     * 审核提现记录
     * @param adminId  操作审核管理员id
     * @param cashWithdrawAuditRecord 提现审核记录数据
     * @return 审核结果
     */
    boolean updateWithdrawalsStatus(Long adminId, CashWithdrawAuditRecord cashWithdrawAuditRecord);
}
