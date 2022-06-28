package com.dolphin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.dolphin.domain.Market;
import com.dolphin.model.R;
import com.dolphin.service.MarketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/markets")
@Api(tags = "交易市场的控制器")
public class MarketController {
    @Autowired
    private MarketService marketService;


    @GetMapping
    @ApiOperation(value = "交易市场的分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curreddnt", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数")
    })
    @PreAuthorize("hasAuthority('trade_market_query')")
    public R<Page<Market>> findByPage(@ApiIgnore Page<Market> page, Long tradeAreaId, Byte status) {
        Page<Market> pageData = marketService.findByPage(page, tradeAreaId, status);
        return R.ok(pageData);
    }

    @PostMapping("/setStatus")
    @ApiOperation(value = "启用/禁用交易市场")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "market", value = "market的json数据")
    })
    @PreAuthorize("hasAuthority('trade_market_update')")
    public R setStatus(@RequestBody Market market) {
        boolean updateById = marketService.updateById(market);
        return updateById?R.ok():R.fail("启用/禁用交易市场状态设置失败");
    }

    @PostMapping
    @ApiOperation(value = "新增一个市场")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "market", value = "marketjson")
    })
    @PreAuthorize("hasAuthority('trade_market_create')")
    public R save(@RequestBody Market market) {
        boolean save = marketService.save(market);
        return save ? R.ok("新增市场成功！"):R.fail("新增市场失败！");
    }

    @PatchMapping
    @ApiOperation(value = "修改一个市场")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "market", value = "marketjson")
    })
    @PreAuthorize("hasAuthority('trade_market_update')")
    public R update(@RequestBody Market market) {
        boolean updateById = marketService.updateById(market);
        return updateById ? R.ok("修改市场成功！"):R.fail("修改市场失败！");
    }
}
