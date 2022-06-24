package com.dolphin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "登录的表单参数")
public class LoginForm extends GeetestForm{

    @ApiModelProperty(value = "国家的电话编号")
    private String countryCode ;

    @ApiModelProperty(value = "用户名称")
    private String username ;

    @ApiModelProperty(value = "用户密码")
    private String password ;

    @ApiModelProperty(value = "用户的uuid")
    private String uuid ;

}
