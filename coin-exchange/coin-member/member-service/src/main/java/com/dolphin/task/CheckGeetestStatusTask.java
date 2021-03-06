package com.dolphin.task;

import com.dolphin.geetest.utils.HttpClientUtils;
import com.dolphin.geetest.utils.PropertiesUtils;
import com.dolphin.geetest.utils.RedisPoolUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class CheckGeetestStatusTask extends TimerTask {

    public void run() {
        String geetest_status = "";
        try {
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("gt", PropertiesUtils.get("geetest.id"));
            String resBody = HttpClientUtils.doGet(PropertiesUtils.get("check.geetest.status.url"), paramMap);
            System.out.println(String.format("gtlog: CheckGeetestStatusTask: 极验云监控，返回body=%s.", resBody));
            JSONObject jsonObject = new JSONObject(resBody);
            geetest_status = jsonObject.getString("status");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (geetest_status != null && !geetest_status.isEmpty()) {
            String key = PropertiesUtils.get("redis.check.geetest.status.key", "REDIS_CHECK_GEETEST_STATUS_KEY");
            int exTime = Integer.parseInt(PropertiesUtils.get("redis.check.geetest.status.extime", "86400"));
            String result = RedisPoolUtils.setex(key, geetest_status, exTime);
            System.out.println(String.format("gtlog: CheckGeetestStatusTask: 正确获取到状态值, 保存值到redis中, value=%s, result=%s.", geetest_status, result));
        } else {
            String key = PropertiesUtils.get("redis.check.geetest.status.key", "REDIS_CHECK_GEETEST_STATUS_KEY");
            int exTime = Integer.parseInt(PropertiesUtils.get("redis.check.geetest.status.extime", "86400"));
            String result = RedisPoolUtils.setex(key, "fail", exTime);
            System.out.println(String.format("gtlog: CheckGeetestStatusTask: 未获取到状态值, 保存默认值fail到redis中, result=%s.", result));
        }
    }

    /**
     * 检测存入redis中的极验云状态标识
     */
    public static boolean checkGeetestStatusRedisFlag() {
        String key = PropertiesUtils.get("redis.check.geetest.status.key", "REDIS_CHECK_GEETEST_STATUS_KEY");
        if ("success".equals(RedisPoolUtils.get(key))) {
            return true;
        } else {
            return false;
        }
    }

}
