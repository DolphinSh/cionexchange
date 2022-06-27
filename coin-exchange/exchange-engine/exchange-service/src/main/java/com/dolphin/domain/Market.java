package com.dolphin.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易对配置信息
 */
@ApiModel(value = "交易对配置信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "market")
public class Market {
    /**
     * 市场ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "市场ID")
    private Long id;

    /**
     * 类型：1-数字货币；2：创新交易
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "类型：1-数字货币；2：创新交易")
    private Byte type;

    /**
     * 交易区域ID
     */
    @TableField(value = "trade_area_id")
    @ApiModelProperty(value = "交易区域ID")
    private Long tradeAreaId;

    /**
     * 卖方市场ID
     */
    @TableField(value = "sell_coin_id")
    @ApiModelProperty(value = "卖方市场ID")
    private Long sellCoinId;

    /**
     * 买方币种ID
     */
    @TableField(value = "buy_coin_id")
    @ApiModelProperty(value = "买方币种ID")
    private Long buyCoinId;

    /**
     * 交易对标识
     */
    @TableField(value = "symbol")
    @ApiModelProperty(value = "交易对标识")
    private String symbol;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 标题
     */
    @TableField(value = "title")
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 市场logo
     */
    @TableField(value = "img")
    @ApiModelProperty(value = "市场logo")
    private String img;

    /**
     * 开盘价格
     */
    @TableField(value = "open_price")
    @ApiModelProperty(value = "开盘价格")
    private BigDecimal openPrice;

    /**
     * 买入手续费率
     */
    @TableField(value = "fee_buy")
    @ApiModelProperty(value = "买入手续费率")
    private BigDecimal feeBuy;

    /**
     * 卖出手续费率
     */
    @TableField(value = "fee_sell")
    @ApiModelProperty(value = "卖出手续费率")
    private BigDecimal feeSell;

    /**
     * 保证金占用比例
     */
    @TableField(value = "margin_rate")
    @ApiModelProperty(value = "保证金占用比例")
    private BigDecimal marginRate;

    /**
     * 单笔最小委托量
     */
    @TableField(value = "num_min")
    @ApiModelProperty(value = "单笔最小委托量")
    private BigDecimal numMin;

    /**
     * 单笔最大委托量
     */
    @TableField(value = "num_max")
    @ApiModelProperty(value = "单笔最大委托量")
    private BigDecimal numMax;

    /**
     * 单笔最小成交额
     */
    @TableField(value = "trade_min")
    @ApiModelProperty(value = "单笔最小成交额")
    private BigDecimal tradeMin;

    /**
     * 单笔最大成交额
     */
    @TableField(value = "trade_max")
    @ApiModelProperty(value = "单笔最大成交额")
    private BigDecimal tradeMax;

    /**
     * 价格小数位
     */
    @TableField(value = "price_scale")
    @ApiModelProperty(value = "价格小数位")
    private Byte priceScale;

    /**
     * 数量小数位
     */
    @TableField(value = "num_scale")
    @ApiModelProperty(value = "数量小数位")
    private Byte numScale;

    /**
     * 合约单位
     */
    @TableField(value = "contract_unit")
    @ApiModelProperty(value = "合约单位")
    private Integer contractUnit;

    /**
     * 点
     */
    @TableField(value = "point_value")
    @ApiModelProperty(value = "点")
    private BigDecimal pointValue;

    /**
     * 合并深度（格式：4,3,2）4:表示为0.0001；3：表示为0.001
     */
    @TableField(value = "merge_depth")
    @ApiModelProperty(value = "合并深度（格式：4,3,2）4:表示为0.0001；3：表示为0.001")
    private String mergeDepth;

    /**
     * 交易时间
     */
    @TableField(value = "trade_time")
    @ApiModelProperty(value = "交易时间")
    private String tradeTime;

    /**
     * 交易周期
     */
    @TableField(value = "trade_week")
    @ApiModelProperty(value = "交易周期")
    private String tradeWeek;

    /**
     * 排序列
     */
    @TableField(value = "sort")
    @ApiModelProperty(value = "排序列")
    private Integer sort;

    /**
     * 状态
     * 0禁用
     * 1启用
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "状态,0禁用,1启用")
    private byte status;

    /**
     * 福汇API交易对
     */
    @TableField(value = "fxcm_symbol")
    @ApiModelProperty(value = "福汇API交易对")
    private String fxcmSymbol;

    /**
     * 对应雅虎金融API交易对
     */
    @TableField(value = "yahoo_symbol")
    @ApiModelProperty(value = "对应雅虎金融API交易对")
    private String yahooSymbol;

    /**
     * 对应阿里云API交易对
     */
    @TableField(value = "aliyun_symbol")
    @ApiModelProperty(value = "对应阿里云API交易对")
    private String aliyunSymbol;

    /**
     * 更新时间
     */
    @TableField(value = "last_update_time", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date lastUpdateTime;

    /**
     * 创建时间
     */
    @TableField(value = "created", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date created;

    public static final String COL_ID = "id";

    public static final String COL_TYPE = "type";

    public static final String COL_TRADE_AREA_ID = "trade_area_id";

    public static final String COL_SELL_COIN_ID = "sell_coin_id";

    public static final String COL_BUY_COIN_ID = "buy_coin_id";

    public static final String COL_SYMBOL = "symbol";

    public static final String COL_NAME = "name";

    public static final String COL_TITLE = "title";

    public static final String COL_IMG = "img";

    public static final String COL_OPEN_PRICE = "open_price";

    public static final String COL_FEE_BUY = "fee_buy";

    public static final String COL_FEE_SELL = "fee_sell";

    public static final String COL_MARGIN_RATE = "margin_rate";

    public static final String COL_NUM_MIN = "num_min";

    public static final String COL_NUM_MAX = "num_max";

    public static final String COL_TRADE_MIN = "trade_min";

    public static final String COL_TRADE_MAX = "trade_max";

    public static final String COL_PRICE_SCALE = "price_scale";

    public static final String COL_NUM_SCALE = "num_scale";

    public static final String COL_CONTRACT_UNIT = "contract_unit";

    public static final String COL_POINT_VALUE = "point_value";

    public static final String COL_MERGE_DEPTH = "merge_depth";

    public static final String COL_TRADE_TIME = "trade_time";

    public static final String COL_TRADE_WEEK = "trade_week";

    public static final String COL_SORT = "sort";

    public static final String COL_STATUS = "status";

    public static final String COL_FXCM_SYMBOL = "fxcm_symbol";

    public static final String COL_YAHOO_SYMBOL = "yahoo_symbol";

    public static final String COL_ALIYUN_SYMBOL = "aliyun_symbol";

    public static final String COL_LAST_UPDATE_TIME = "last_update_time";

    public static final String COL_CREATED = "created";
}