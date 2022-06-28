package com.dolphin.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiOperation(value = "coin 的RPC 传输对象")
public class CoinDto {
    @ApiModelProperty(value = "币种的Id")
    private Long id;

    @ApiModelProperty(value = "币种的名称")
    private String name;

    @ApiModelProperty(value = "币种的标题")
    private String title;

    @ApiModelProperty(value = "币种的Logo")
    private String img;


    @ApiModelProperty(value = "最小提现单位")
    private BigDecimal baseAmount;


    @ApiModelProperty(value = "单笔最小提现数量")
    private BigDecimal minAmount;

    @ApiModelProperty(value = "单笔最大提现数量")
    private BigDecimal maxAmount;

    @ApiModelProperty(value = "当日最大提现数量")
    private BigDecimal dayMaxAmount;
}
