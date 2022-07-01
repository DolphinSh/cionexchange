package com.dolphin.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.dolphin.domain.Notice;
import com.dolphin.model.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 完成文件上传的功能
 */
@RestController
@Api(tags = "文件上传")
public class FileController {


    @Autowired
    private OSS ossClient; //Spring-cloud-alibaba-oss 会自动注入该对象，报红不要紧

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @Value("${oss.bucket.name:coin-excahege}")
    private String bucketName;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endPoint;

    @Value(("${oss.callback.url:http://125.71.130.179:9000}"))
    private String ossCallbackUrl;

    @ApiOperation(value = "上传文件")
    @PostMapping("/image/AliYunImgUpload")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "你要上传的文件")
    })
    public R<String> fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = DateUtil.today().replaceAll("-", "/") + "/" + file.getOriginalFilename();
        ossClient.putObject(bucketName, fileName, file.getInputStream());
        return R.ok("https://" + bucketName + "." + endPoint + "/" + fileName); //能使用浏览器访问到文件路径http://xxx.com/路径
    }

    @GetMapping("/image/pre/upload")
    @ApiOperation(value = "文件的上传获取票据")
    public R<Object> preUploadPolicy(){
        String fileDir = DateUtil.today().replace("-", "/") + '/';
        Map<String, String> policy = getPolicy(30L, 10 * 1024 * 1024L, fileDir);
        if (!MapUtil.isEmpty(policy)) {
            return R.ok(policy);
        }else {
            return R.fail("文件上传失败");
        }
    }

    public Map<String,String> getPolicy(long expireTime, long maxFileSize, String dir) {
        try {
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            //设置文件上传的最大体积
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxFileSize);
            //设置文件上传到那个文件夹
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);//设置该policy 的有效时间
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            //返回值
            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", "https://" + bucketName +"."+ endPoint);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));

            JSONObject jasonCallback = new JSONObject();
            jasonCallback.put("callbackUrl", ossCallbackUrl); //当前端把文件上传到oss 服务器成功后，服务器会把想回调的callbackUrl 发一个post
            //请求来告诉后端服务器用户上传文件的情况。
            jasonCallback.put("callbackBody",
                    "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
            jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
            String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
            respMap.put("callback", base64CallbackBody);

            return respMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
