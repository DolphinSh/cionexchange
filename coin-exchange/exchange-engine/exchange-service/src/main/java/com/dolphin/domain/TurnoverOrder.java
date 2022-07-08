package com.dolphin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 成交订单
    */
@ApiModel(value="成交订单")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "turnover_order")
public class TurnoverOrder {
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value="")
    private Long id;

    /**
     * 市场ID
     */
    @TableField(value = "market_id")
    @ApiModelProperty(value="市场ID")
    private Long marketId;

    /**
     * 交易对类型：1-币币交易；2-创新交易；
     */
    @TableField(value = "market_type")
    @ApiModelProperty(value="交易对类型：1-币币交易；2-创新交易；")
    private Integer marketType;

    /**
     * 交易类型:1 买 2卖
     */
    @TableField(value = "trade_type")
    @ApiModelProperty(value="交易类型:1 买 2卖")
    private Integer tradeType;

    /**
     * 交易对标识符
     */
    @TableField(value = "symbol")
    @ApiModelProperty(value="交易对标识符")
    private String symbol;

    /**
     * 交易对名称
     */
    @TableField(value = "market_name")
    @ApiModelProperty(value="交易对名称")
    private String marketName;

    /**
     * 卖方用户ID
     */
    @TableField(value = "sell_user_id")
    @ApiModelProperty(value="卖方用户ID")
    private Long sellUserId;

    /**
     * 卖方币种ID
     */
    @TableField(value = "sell_coin_id")
    @ApiModelProperty(value="卖方币种ID")
    private Long sellCoinId;

    /**
     * 卖方委托订单ID
     */
    @TableField(value = "sell_order_id")
    @ApiModelProperty(value="卖方委托订单ID")
    private Long sellOrderId;

    /**
     * 卖方委托价格
     */
    @TableField(value = "sell_price")
    @ApiModelProperty(value="卖方委托价格")
    private BigDecimal sellPrice;

    /**
     * 卖方手续费率
     */
    @TableField(value = "sell_fee_rate")
    @ApiModelProperty(value="卖方手续费率")
    private BigDecimal sellFeeRate;

    /**
     * 卖方委托数量
     */
    @TableField(value = "sell_volume")
    @ApiModelProperty(value="卖方委托数量")
    private BigDecimal sellVolume;

    /**
     * 买方用户ID
     */
    @TableField(value = "buy_user_id")
    @ApiModelProperty(value="买方用户ID")
    private Long buyUserId;

    /**
     * 买方币种ID
     */
    @TableField(value = "buy_coin_id")
    @ApiModelProperty(value="买方币种ID")
    private Long buyCoinId;

    /**
     * 买方委托订单ID
     */
    @TableField(value = "buy_order_id")
    @ApiModelProperty(value="买方委托订单ID")
    private Long buyOrderId;

    /**
     * 买方委托数量
     */
    @TableField(value = "buy_volume")
    @ApiModelProperty(value="买方委托数量")
    private BigDecimal buyVolume;

    /**
     * 买方委托价格
     */
    @TableField(value = "buy_price")
    @ApiModelProperty(value="买方委托价格")
    private BigDecimal buyPrice;

    /**
     * 买方手续费率
     */
    @TableField(value = "buy_fee_rate")
    @ApiModelProperty(value="买方手续费率")
    private BigDecimal buyFeeRate;

    /**
     * 委托订单ID
     */
    @TableField(value = "order_id")
    @ApiModelProperty(value="委托订单ID")
    private Long orderId;

    /**
     * 成交总额
     */
    @TableField(value = "amount")
    @ApiModelProperty(value="成交总额")
    private BigDecimal amount;

    /**
     * 成交价格
     */
    @TableField(value = "price")
    @ApiModelProperty(value="成交价格")
    private BigDecimal price;

    /**
     * 成交数量
     */
    @TableField(value = "volume")
    @ApiModelProperty(value="成交数量")
    private BigDecimal volume;

    /**
     * 成交卖出手续费
     */
    @TableField(value = "deal_sell_fee")
    @ApiModelProperty(value="成交卖出手续费")
    private BigDecimal dealSellFee;

    /**
     * 成交卖出手续费率
     */
    @TableField(value = "deal_sell_fee_rate")
    @ApiModelProperty(value="成交卖出手续费率")
    private BigDecimal dealSellFeeRate;

    /**
     * 成交买入手续费
     */
    @TableField(value = "deal_buy_fee")
    @ApiModelProperty(value="成交买入手续费")
    private BigDecimal dealBuyFee;

    /**
     * 成交买入成交率费
     */
    @TableField(value = "deal_buy_fee_rate")
    @ApiModelProperty(value="成交买入成交率费")
    private BigDecimal dealBuyFeeRate;

    /**
     * 状态0待成交，1已成交，2撤销，3.异常
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态0待成交，1已成交，2撤销，3.异常")
    private Boolean status;

    /**
     * 更新时间
     */
    @TableField(value = "last_update_time")
    @ApiModelProperty(value="更新时间")
    private Date lastUpdateTime;

    /**
     * 创建时间
     */
    @TableField(value = "created")
    @ApiModelProperty(value="创建时间")
    private Date created;

    public static final String COL_ID = "id";

    public static final String COL_MARKET_ID = "market_id";

    public static final String COL_MARKET_TYPE = "market_type";

    public static final String COL_TRADE_TYPE = "trade_type";

    public static final String COL_SYMBOL = "symbol";

    public static final String COL_MARKET_NAME = "market_name";

    public static final String COL_SELL_USER_ID = "sell_user_id";

    public static final String COL_SELL_COIN_ID = "sell_coin_id";

    public static final String COL_SELL_ORDER_ID = "sell_order_id";

    public static final String COL_SELL_PRICE = "sell_price";

    public static final String COL_SELL_FEE_RATE = "sell_fee_rate";

    public static final String COL_SELL_VOLUME = "sell_volume";

    public static final String COL_BUY_USER_ID = "buy_user_id";

    public static final String COL_BUY_COIN_ID = "buy_coin_id";

    public static final String COL_BUY_ORDER_ID = "buy_order_id";

    public static final String COL_BUY_VOLUME = "buy_volume";

    public static final String COL_BUY_PRICE = "buy_price";

    public static final String COL_BUY_FEE_RATE = "buy_fee_rate";

    public static final String COL_ORDER_ID = "order_id";

    public static final String COL_AMOUNT = "amount";

    public static final String COL_PRICE = "price";

    public static final String COL_VOLUME = "volume";

    public static final String COL_DEAL_SELL_FEE = "deal_sell_fee";

    public static final String COL_DEAL_SELL_FEE_RATE = "deal_sell_fee_rate";

    public static final String COL_DEAL_BUY_FEE = "deal_buy_fee";

    public static final String COL_DEAL_BUY_FEE_RATE = "deal_buy_fee_rate";

    public static final String COL_STATUS = "status";

    public static final String COL_LAST_UPDATE_TIME = "last_update_time";

    public static final String COL_CREATED = "created";
}