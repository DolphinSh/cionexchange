package com.dolphin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.EntrustOrder;
import com.dolphin.model.R;
import com.dolphin.service.EntrustOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/entrustOrders")
@Api(tags = "委托记录")
public class EntrustOrderController {
    @Autowired
    private EntrustOrderService entrustOrderService ;

    @GetMapping
    @ApiOperation(value = "查询用户的委托记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current" ,value = "当前页") ,
            @ApiImplicitParam(name = "size" ,value = "显示的条数") ,
            @ApiImplicitParam(name = "symbol" ,value = "交易对") ,
            @ApiImplicitParam(name = "symbol" ,value = "类型") ,
    })
    public R<Page<EntrustOrder>> findByPage(@ApiIgnore Page<EntrustOrder> page, String symbol , Integer type){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()) ;
        Page<EntrustOrder> entrustOrderPage = entrustOrderService.findByPage(page,userId,symbol,type) ;
        return R.ok(entrustOrderPage) ;
    }
}
