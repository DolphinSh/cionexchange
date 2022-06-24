package com.dolphin.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.alicloud.sms.ISmsService;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.dolphin.domain.Config;
import com.dolphin.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.domain.Sms;
import com.dolphin.mapper.SmsMapper;
import com.dolphin.service.impl.SmsService;

@Service
@Slf4j
public class SmsServiceImpl extends ServiceImpl<SmsMapper, Sms> implements SmsService{


    @Autowired
    private ISmsService smsService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 发送一个短信内容
     *
     * @param sms 短信内容
     * @return 是否发送成功
     */
    @Override
    public boolean sendSms(Sms sms) {
        log.info("发送短信{}", JSON.toJSONString(sms,true));
        SendSmsRequest request = buildSmsRequest(sms);
        try {
            SendSmsResponse sendSmsResponse = smsService.sendSmsRequest(request);
            log.info("发送的结果为{}",JSON.toJSONString(sendSmsResponse,true));
            String code = sendSmsResponse.getCode();
            if ("OK".equals(code)){
                sms.setStatus(1);
                return save(sms);
            }else {
                return false;
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 构建发送短信的请求对象
     * @param sms 短信对象
     * @return
     */
    private SendSmsRequest buildSmsRequest(Sms sms) {
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setPhoneNumbers(sms.getMobile()); //发送给谁
        Config signConfig = configService.getConfigByCode("SIGN");
        sendSmsRequest.setSignName(signConfig.getValue()); //设置签名 --- 公司里面不会随便改变

        Config configByCode = configService.getConfigByCode(sms.getTemplateCode());
        if (configByCode == null){
            throw new IllegalArgumentException("签名不存在！");
        }
        sendSmsRequest.setTemplateCode(configByCode.getValue());
        String code = RandomUtil.randomNumbers(4);
        //需要把code 保存到redis里面
        // key: SMS:VERIFY_OLD_PHONE:15827293117     value: 1234
        redisTemplate.opsForValue().set("SMS:" + sms.getTemplateCode() + ":" + sms.getMobile(), code,5, TimeUnit.MINUTES);
        sendSmsRequest.setTemplateParam("{\"code\":\"" + code + "\"}");
        String desc = configByCode.getDesc(); //  sign:您的验证码${code}，该验证码5分钟内有效，请勿泄漏于他人！
        String content = signConfig.getValue() + ":" + desc.replaceAll("\\$\\{code\\}", code);
        sms.setContent("1234");
        return sendSmsRequest;
    }
}
