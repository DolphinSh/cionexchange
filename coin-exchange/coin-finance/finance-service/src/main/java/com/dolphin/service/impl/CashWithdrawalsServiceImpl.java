package com.dolphin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.Account;
import com.dolphin.domain.CashWithdrawAuditRecord;
import com.dolphin.domain.Config;
import com.dolphin.dto.UserBankDto;
import com.dolphin.dto.UserDto;
import com.dolphin.feign.UserBankServiceFeign;
import com.dolphin.feign.UserServiceFeign;
import com.dolphin.mapper.CashWithdrawAuditRecordMapper;
import com.dolphin.model.CashSellParam;
import com.dolphin.service.AccountService;
import com.dolphin.service.ConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.CashWithdrawals;
import com.dolphin.mapper.CashWithdrawalsMapper;
import com.dolphin.service.CashWithdrawalsService;
import org.springframework.util.CollectionUtils;

@Service
public class CashWithdrawalsServiceImpl extends ServiceImpl<CashWithdrawalsMapper, CashWithdrawals> implements CashWithdrawalsService {
    @Autowired
    private UserServiceFeign userServiceFeign;

    @Autowired
    private UserBankServiceFeign userBankServiceFeign;

    @Autowired
    private CashWithdrawAuditRecordMapper cashWithdrawAuditRecordMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ConfigService configService;

    @CreateCache(name = "CASH_WITHDRAWALS_LOCK:", expire = 100, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.BOTH)
    private Cache<String, String> cache;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * ?????????????????????
     *
     * @param page      ????????????
     * @param userId    ?????????id
     * @param userName  ???????????????
     * @param mobile    ??????????????????
     * @param status    ???????????????
     * @param numMin    ?????????????????????
     * @param numMax    ?????????????????????
     * @param startTime ?????????????????????
     * @param endTime   ?????????????????????
     * @return
     */
    @Override
    public Page<CashWithdrawals> findByPage(Page<CashWithdrawals> page, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime) {
        // 1 ?????????????????????
        Map<Long, UserDto> basicUsers = null;
        LambdaQueryWrapper<CashWithdrawals> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (userId != null || !StringUtils.isEmpty(userName) || !StringUtils.isEmpty(mobile)) {
            basicUsers = userServiceFeign.getBasicUsers(userId == null ? null : Arrays.asList(userId), userName, mobile);
            if (CollectionUtils.isEmpty(basicUsers)) {
                return page;
            }
            Set<Long> userIds = basicUsers.keySet();
            lambdaQueryWrapper.in(CashWithdrawals::getUserId, userIds);
        }
        // 2 ????????????????????????
        lambdaQueryWrapper.eq(status != null, CashWithdrawals::getStatus, status)
                .between(
                        !(StringUtils.isEmpty(numMin) || StringUtils.isEmpty(numMax)),
                        CashWithdrawals::getNum,
                        new BigDecimal(StringUtils.isEmpty(numMin) ? "0" : numMin),
                        new BigDecimal(StringUtils.isEmpty(numMax) ? "0" : numMax)
                )
                .between(
                        !(StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)),
                        CashWithdrawals::getCreated,
                        startTime, endTime + " 23:59:59"
                );
        Page<CashWithdrawals> pageData = page(page, lambdaQueryWrapper);
        List<CashWithdrawals> records = pageData.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<Long> userIds = records.stream().map(CashWithdrawals::getUserId).collect(Collectors.toList());
            if (basicUsers == null) {
                basicUsers = userServiceFeign.getBasicUsers(userIds, null, null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(cashWithdrawals -> {
                UserDto userDto = finalBasicUsers.get(cashWithdrawals.getUserId());
                if (userDto != null) {
                    cashWithdrawals.setUsername(userDto.getUsername());
                    cashWithdrawals.setRealName(userDto.getRealName());
                }
            });
        }
        return pageData;
    }

    /**
     * ??????????????????
     *
     * @param adminId                 ?????????????????????id
     * @param cashWithdrawAuditRecord ????????????????????????
     * @return ????????????
     */
    @Override
    public boolean updateWithdrawalsStatus(Long adminId, CashWithdrawAuditRecord cashWithdrawAuditRecord) {
        boolean tryLockAndRun = cache.tryLockAndRun(cashWithdrawAuditRecord.getId().toString(), 300, TimeUnit.SECONDS, () -> {
            CashWithdrawals cashWithdrawals = getById(cashWithdrawAuditRecord.getId());
            if (cashWithdrawals == null) {
                throw new IllegalArgumentException("?????????????????????????????????");
            }
            // ????????????????????????
            CashWithdrawAuditRecord cashWithdrawAuditRecordNew = new CashWithdrawAuditRecord();
            cashWithdrawAuditRecordNew.setAuditUserId(adminId);
            cashWithdrawAuditRecordNew.setRemark(cashWithdrawAuditRecord.getRemark());
            cashWithdrawAuditRecordNew.setCreated(DateUtil.date());
            cashWithdrawAuditRecordNew.setStatus(cashWithdrawAuditRecord.getStatus());
            Integer step = cashWithdrawals.getStep() + 1;
            cashWithdrawAuditRecordNew.setStep(step.byteValue());
            cashWithdrawAuditRecordNew.setOrderId(cashWithdrawals.getId());

            // ??????????????????
            int count = cashWithdrawAuditRecordMapper.insert(cashWithdrawAuditRecordNew);
            if (count > 0) {
                cashWithdrawals.setStatus(cashWithdrawAuditRecord.getStatus());
                cashWithdrawals.setRemark(cashWithdrawAuditRecord.getRemark());
                cashWithdrawals.setLastTime(DateUtil.date());
                cashWithdrawals.setAccountId(adminId); //
                cashWithdrawals.setStep(step.byteValue());
                boolean updateById = updateById(cashWithdrawals);   // ????????????
                if (updateById) {
                    // ???????????? withdrawals_out
                    Boolean isPass = accountService.decreaseAccountAmount(
                            adminId, cashWithdrawals.getUserId(), cashWithdrawals.getCoinId(),
                            cashWithdrawals.getId(), cashWithdrawals.getNum(), cashWithdrawals.getFee(),
                            cashWithdrawals.getRemark(), "withdrawals_out", (byte) 2
                    );
                }
            }
        });
        return tryLockAndRun;
    }

    /**
     * ?????????????????????????????????
     *
     * @param page   ????????????
     * @param userId ?????????id
     * @param status ???????????????
     * @return
     */
    @Override
    public Page<CashWithdrawals> findCashWithdrawals(Page<CashWithdrawals> page, Long userId, Byte status) {
        return page(page, new LambdaQueryWrapper<CashWithdrawals>()
                .eq(CashWithdrawals::getUserId, userId)
                .eq(status != null, CashWithdrawals::getStatus, status));
    }

    /**
     * ??????????????????
     *
     * @param userId        ?????????id
     * @param cashSellParam GCN???????????????
     * @return ????????????????????????
     */
    @Override
    public boolean sell(Long userId, CashSellParam cashSellParam) {
        //1 ????????????
        checkCashSellParam(cashSellParam);
        Map<Long, UserDto> basicUsers = userServiceFeign.getBasicUsers(Arrays.asList(userId), null, null);
        if (CollectionUtils.isEmpty(basicUsers)) {
            throw new IllegalArgumentException("?????????id??????");
        }
        UserDto userDto = basicUsers.get(userId);
        // 2 ???????????????
        validatePhoneCode(userDto.getMobile(), cashSellParam.getValidateCode());
        // 3 ????????????
        checkUserPayPassword(userDto.getPaypassword(), cashSellParam.getPayPassword());
        // 4 ????????????????????????????????????
        UserBankDto userBankInfo = userBankServiceFeign.getUserBankInfo(userId);
        if (userBankInfo == null) {
            throw new IllegalArgumentException("????????????????????????????????????");
        }
        //?????????
        String remark = RandomUtil.randomNumbers(6);
        // 5 ???????????????????????????????????????
        BigDecimal amount = getCashWithdrawalsAmount(cashSellParam.getNum());
        // 6 ????????????????????????
        BigDecimal fee = getCashWithdrawalsFee(amount);
        // 7 ?????????????????????ID
        Account account = accountService.findByUserAndCoin(userId, "GCN");
        // 8 ???????????????
        CashWithdrawals cashWithdrawals = new CashWithdrawals();
        cashWithdrawals.setUserId(userId);
        cashWithdrawals.setAccountId(account.getId());
        cashWithdrawals.setCoinId(cashSellParam.getCoinId());
        cashWithdrawals.setStatus((byte) 0);
        cashWithdrawals.setStep((byte) 1);
        cashWithdrawals.setNum(cashSellParam.getNum());
        cashWithdrawals.setMum(amount.subtract(fee)); // ???????????? = amount-fee
        cashWithdrawals.setFee(fee);
        cashWithdrawals.setBank(userBankInfo.getBank());
        cashWithdrawals.setBankCard(userBankInfo.getBankCard());
        cashWithdrawals.setBankAddr(userBankInfo.getBankAddr());
        cashWithdrawals.setBankProv(userBankInfo.getBankProv());
        cashWithdrawals.setBankCity(userBankInfo.getBankCity());
        cashWithdrawals.setTruename(userBankInfo.getRealName());
        cashWithdrawals.setRemark(remark);
        boolean save = save(cashWithdrawals);
        if (save){
            //???????????????--account-->accountDetail
            accountService.lockUserAmount(userId,
                    cashWithdrawals.getCoinId(),
                    cashWithdrawals.getMum(),
                    "withdrawals_out",
                    cashWithdrawals.getId(),
                    cashWithdrawals.getFee()
            );
        }
        return save;
    }

    /**
     * ????????????????????????
     * @param amount
     * @return
     */
    private BigDecimal getCashWithdrawalsFee(BigDecimal amount) {
        // 1 ???????????????* ?????? = ?????????
        // 2 ???????????????---->???????????????????????????

        // ?????????????????????
        Config withdrawMinPoundage = configService.getConfigByCode("WITHDRAW_MIN_POUNDAGE");
        BigDecimal withdrawMinPoundageFee = new BigDecimal(withdrawMinPoundage.getValue());

        // ???????????????
        Config withdrawPoundageRate = configService.getConfigByCode("WITHDRAW_POUNDAGE_RATE");
        // ??????????????????????????????
        BigDecimal poundageFee = amount.multiply(new BigDecimal(withdrawPoundageRate.getValue())).setScale(2, RoundingMode.HALF_UP);
        //min ???2 ???????????????
        return poundageFee.min(withdrawMinPoundageFee).equals(poundageFee) ? withdrawMinPoundageFee : poundageFee;
    }

    /**
     * ???????????????????????????????????????
     * @param num
     * @return
     */
    private BigDecimal getCashWithdrawalsAmount(BigDecimal num) {
        Config rateConfig = configService.getConfigByCode("USDT2CNY");
        return num.multiply(new BigDecimal(rateConfig.getValue())).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * ??????????????????
     *
     * @param payDBPassword ???????????????
     * @param payPassword ????????????????????????
     */
    private void checkUserPayPassword(String payDBPassword, String payPassword) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(payPassword, payDBPassword);
        if (!matches) {
            throw new IllegalArgumentException("??????????????????");
        }
    }

    /**
     * ?????????????????????
     *
     * @param mobile
     * @param validateCode
     */
    private void validatePhoneCode(String mobile, String validateCode) {

        // ??????:SMS:CASH_WITHDRAWS:mobile
        String code = redisTemplate.opsForValue().get("SMS:CASH_WITHDRAWS:" + mobile);
        if (!validateCode.equals(code)) {
            throw new IllegalArgumentException("???????????????");
        }
    }

    /**
     * ????????????????????????
     *
     * @param cashSellParam
     */
    private void checkCashSellParam(CashSellParam cashSellParam) {
        //1 ????????????
        Config cashWithdrawalsStatus = configService.getConfigByCode("WITHDRAW_STATUS");
        if (Integer.valueOf(cashWithdrawalsStatus.getValue()) != 1) {
            throw new IllegalArgumentException("?????????????????????");
        }
        //2 ???????????????
        @NotNull BigDecimal cashSellParamNum = cashSellParam.getNum();
        //2.1 ????????????????????? 100
        Config cashWithdrawalsConfigMin = configService.getConfigByCode("WITHDRAW_MIN_AMOUNT");
        // 101
        if (cashSellParamNum.compareTo(new BigDecimal(cashWithdrawalsConfigMin.getValue())) < 0) {
            throw new IllegalArgumentException("????????????????????????");
        }
        //2.2 ?????????????????????
        //201
        Config cashWithdrawalsConfigMax = configService.getConfigByCode("WITHDRAW_MAX_AMOUNT");
        if (cashSellParamNum.compareTo(new BigDecimal(cashWithdrawalsConfigMax.getValue())) >= 0) {
            throw new IllegalArgumentException("????????????????????????");
        }
    }
}
