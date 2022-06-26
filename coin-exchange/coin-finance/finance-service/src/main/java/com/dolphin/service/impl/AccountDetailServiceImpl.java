package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.dto.UserDto;
import com.dolphin.feign.UserServiceFeign;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.AccountDetailMapper;
import com.dolphin.domain.AccountDetail;
import com.dolphin.service.AccountDetailService;
import org.springframework.util.CollectionUtils;

@Service
public class AccountDetailServiceImpl extends ServiceImpl<AccountDetailMapper, AccountDetail> implements AccountDetailService {
    @Autowired
    private UserServiceFeign userServiceFeign;

    /**
     * 分页条件查询充值记录
     *
     * @param page        分页参数
     * @param coinId      币种的Id
     * @param accountId   账号的Id
     * @param userId      用户的id
     * @param userName    用户的名称
     * @param mobile      用户的手机
     * @param amountStart 金额最小值
     * @param amountEnd   金额的最大值
     * @param startTime   起始时间
     * @param endTime     截至时间
     * @return
     */
    @Override
    public Page<AccountDetail> findByPage(Page<AccountDetail> page, Long coinId, Long accountId, Long userId, String userName, String mobile, String amountStart, String amountEnd, String startTime, String endTime) {
        LambdaQueryWrapper<AccountDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //1 有userId userName mobile 用户的信息的用户查询
        Map<Long, UserDto> basicUsers = null;
        if (userId != null || !StringUtils.isEmpty(userName) || !StringUtils.isEmpty(mobile)) {
            basicUsers = userServiceFeign.getBasicUsers(userId == null ? null : Arrays.asList(userId), userName, mobile);
            if (CollectionUtils.isEmpty(basicUsers)) {
                return page;
            }
            Set<Long> userIds = basicUsers.keySet();// 用户的Id 集合
            lambdaQueryWrapper.in(AccountDetail::getUserId, userIds);
        }

        lambdaQueryWrapper.eq(accountId != null, AccountDetail::getAccountId, accountId)
                .eq(coinId != null, AccountDetail::getCoinId, coinId)
                .between(
                        !(StringUtils.isEmpty(amountStart) || StringUtils.isEmpty(amountEnd)),
                        AccountDetail::getAmount,
                        new BigDecimal(StringUtils.isEmpty(amountStart) ? "0" : amountStart),
                        new BigDecimal(StringUtils.isEmpty(amountEnd) ? "0" : amountEnd)
                )
                .between(
                        !(StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)),
                        AccountDetail::getCreated,
                        startTime, endTime + " 23:59:59"
                );
        Page<AccountDetail> pageData = page(page, lambdaQueryWrapper);
        List<AccountDetail> records = pageData.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<Long> userIds = records.stream().map(AccountDetail::getUserId).collect(Collectors.toList());
            if (basicUsers == null) {
                basicUsers = userServiceFeign.getBasicUsers(userIds, null, null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(accountDetail -> {
                UserDto userDto = finalBasicUsers.get(accountDetail.getUserId());
                if (userDto != null) {
                    accountDetail.setUsername(userDto.getUsername());
                    accountDetail.setRealName(userDto.getRealName());
                }

            });
        }
        return pageData;
    }
}
