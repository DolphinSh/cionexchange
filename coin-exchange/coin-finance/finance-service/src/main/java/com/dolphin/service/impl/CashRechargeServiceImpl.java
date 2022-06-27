package com.dolphin.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.CashRechargeAuditRecord;
import com.dolphin.dto.UserDto;
import com.dolphin.feign.UserServiceFeign;
import com.dolphin.mapper.CashRechargeAuditRecordMapper;
import com.dolphin.service.AccountService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.CashRecharge;
import com.dolphin.mapper.CashRechargeMapper;
import com.dolphin.service.CashRechargeService;
import org.springframework.util.CollectionUtils;


@Service
public class CashRechargeServiceImpl extends ServiceImpl<CashRechargeMapper, CashRecharge> implements CashRechargeService {


    @Autowired
    private UserServiceFeign userServiceFeign;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CashRechargeAuditRecordMapper cashRechargeAuditRecordMapper;

    @CreateCache(name = "CASH_RECHARGE_LOCK:", expire = 100, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.BOTH)
    private Cache<String, String> cache;

    /**
     * 分页条件查询充值记录
     *
     * @param page      分页参数
     * @param coinId    币种的Id
     * @param userId    用户的Id
     * @param userName  用户的名称
     * @param mobile    用户的手机号
     * @param status    充值的状态
     * @param numMin    充值的最小金额
     * @param numMax    充值的最大金额
     * @param startTime 充值的开始时间
     * @param endTime   充值的截至时间
     * @return
     */
    @Override
    public Page<CashRecharge> findByPage(Page<CashRecharge> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime) {
        LambdaQueryWrapper<CashRecharge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 1 若用户本次的查询中,带了用户的信息userId, userName,mobile ----> 本质就是要把用户的Id 放在查询条件里面
        Map<Long, UserDto> basicUsers = null;
        if (userId != null || !StringUtils.isEmpty(userName) || !StringUtils.isEmpty(mobile)) {
            //使用用户的信息查询
            basicUsers = userServiceFeign.getBasicUsers(userId == null ? null : Arrays.asList(userId), userName, mobile);
            if (CollectionUtils.isEmpty(basicUsers)) {
                return page;
            }
            Set<Long> userIds = basicUsers.keySet(); //需要远程调用查询用户信息
            lambdaQueryWrapper.in(!CollectionUtils.isEmpty(userIds), CashRecharge::getUserId, userIds);

        }
        // 2 若用户本次的查询中,没有带了用户的信息,就没有1 中的三个值
        lambdaQueryWrapper.eq(coinId != null, CashRecharge::getCoinId, coinId)
                .eq(status != null, CashRecharge::getStatus, status)
                .between(
                        !(StringUtils.isEmpty(numMin) || StringUtils.isEmpty(numMax)),
                        CashRecharge::getNum,
                        new BigDecimal(StringUtils.isEmpty(numMin) ? "0" : numMin),
                        new BigDecimal(StringUtils.isEmpty(numMax) ? "0" : numMax)
                )
                .between(
                        !(StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)),
                        CashRecharge::getCreated,
                        startTime, endTime + " 23:59:59"
                );
        Page<CashRecharge> cashRechargePage = page(page, lambdaQueryWrapper);
        List<CashRecharge> records = cashRechargePage.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<Long> userIds = records.stream().map(CashRecharge::getUserId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(basicUsers)) {
                basicUsers = userServiceFeign.getBasicUsers(userIds, null, null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(cashRecharge -> {
                UserDto userDto = finalBasicUsers.get(cashRecharge.getUserId());
                if (userDto != null) {
                    cashRecharge.setUsername(userDto.getUsername()); // 远程调用查询用户的信息
                    cashRecharge.setRealName(userDto.getRealName());
                }
            });
        }
        return cashRechargePage;
    }

    /**
     * 现金的充值审核
     *
     * @param userId                  审核人Id
     * @param cashRechargeAuditRecord 审核的数据
     * @return 是否审核成功
     */
    @Override
    public boolean cashRechargeAudit(Long userId, CashRechargeAuditRecord cashRechargeAuditRecord) {
        //1 当一个员工审核时，另一个员工不能再审核
        //锁的粒度为 CASH_RECHARGE_LOCK: 1234564
        boolean tryLockAndRun = cache.tryLockAndRun(cashRechargeAuditRecord.getId().toString(), 300, TimeUnit.SECONDS, () -> {
            Long rechargeId = cashRechargeAuditRecord.getId();
            CashRecharge cashRecharge = getById(rechargeId);
            if (cashRecharge == null) {
                new IllegalArgumentException("重置记录不存在！");
            }
            Byte status = cashRecharge.getStatus();
            if (status == 1) {
                new IllegalArgumentException("审核已经通过！");
            }
            //构建审核数据
            CashRechargeAuditRecord cashRechargeAuditRecordDb = new CashRechargeAuditRecord();
            cashRechargeAuditRecordDb.setAuditUserId(userId);
            cashRechargeAuditRecordDb.setStatus(cashRechargeAuditRecord.getStatus());
            cashRechargeAuditRecordDb.setRemark(cashRechargeAuditRecord.getRemark());
            cashRechargeAuditRecordDb.setStep((byte) (cashRecharge.getStep() + 1));
            //保存审核记录
            int insert = cashRechargeAuditRecordMapper.insert(cashRechargeAuditRecordDb);
            if (insert == 0) {
                new IllegalArgumentException("审核记录保存失败！");
            }

            cashRecharge.setStatus(cashRechargeAuditRecord.getStatus());
            cashRecharge.setAuditRemark(cashRechargeAuditRecord.getRemark());
            cashRecharge.setStep((byte) (cashRecharge.getStep() + 1));
            // 管理员没有通过审核 2 = 没有通过
            if (cashRechargeAuditRecord.getStatus() == 2) {
                updateById(cashRecharge);
            } else if (cashRechargeAuditRecord.getStatus() == 1) {
                // 管理员通过审核 ,给用户进行充值
                Boolean isOk = accountService.transferAccountAmount(userId,
                        cashRecharge.getUserId(), cashRecharge.getCoinId(), cashRecharge.getId(), cashRecharge.getNum(), cashRecharge.getFee(),
                        "充值", "recharge_into",(byte)1);
                if (isOk) {
                    cashRecharge.setLastTime(DateUtil.date());
                    updateById(cashRecharge);
                }
            }
        });
        return tryLockAndRun;
    }
}
