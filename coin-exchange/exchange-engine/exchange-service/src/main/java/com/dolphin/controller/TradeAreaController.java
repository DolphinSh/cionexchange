package com.dolphin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.TradeArea;
import com.dolphin.model.R;
import com.dolphin.service.TradeAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/tradeAreas") //tradeAreas
@Api(tags = "交易区域的数据接口")
public class TradeAreaController {

    @Autowired
    private TradeAreaService tradeAreaService;

    @GetMapping
    @ApiOperation(value = "交易区域的分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数"),
            @ApiImplicitParam(name = "name", value = "交易区域的名称"),
            @ApiImplicitParam(name = "status", value = "交易区域的状态"),
    })
    @PreAuthorize("hasAuthority('trade_area_query')")
    public R<Page<TradeArea>> findByPage(@ApiIgnore Page<TradeArea> page, String name, Byte status) {
        Page<TradeArea> pageData = tradeAreaService.findByPage(page, name, status);
        return R.ok(pageData);
    }

    @PostMapping
    @ApiOperation(value = "新增一个交易区域")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tradeArea", value = "tradeAreajson")
    })
    @PreAuthorize("hasAuthority('trade_area_create')")
    public R save(@RequestBody TradeArea tradeArea) {
        boolean save = tradeAreaService.save(tradeArea);
        return save ? R.ok() : R.fail("新增交易区域失败!");
    }

    @PatchMapping
    @ApiOperation(value = "修改一个交易区域")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tradeArea", value = "tradeAreajson")
    })
    @PreAuthorize("hasAuthority('trade_area_update')")
    public R update(@RequestBody TradeArea tradeArea) {
        boolean update = tradeAreaService.updateById(tradeArea);
        return update ? R.ok() : R.fail("修改交易区域失败!");
    }

    @PostMapping("/status")
    @ApiOperation(value = "修改一个交易区域的状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tradeArea", value = "tradeAreajson")
    })
    @PreAuthorize("hasAuthority('trade_area_update')")
    public R updateStatus(@RequestBody TradeArea tradeArea) {
        boolean update = tradeAreaService.updateById(tradeArea);
        return update ? R.ok() : R.fail("修改交易区域的状态失败!");
    }
}
