package com.dolphin.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.CashRechargeAuditRecord;
import com.dolphin.mapper.CashRechargeAuditRecordMapper;
import com.dolphin.service.CashRechargeAuditRecordService;
@Service
public class CashRechargeAuditRecordServiceImpl extends ServiceImpl<CashRechargeAuditRecordMapper, CashRechargeAuditRecord> implements CashRechargeAuditRecordService{

}
