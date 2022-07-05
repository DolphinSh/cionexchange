package com.dolphin.boot;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dolphin.disruptor.DisruptorTemplate;
import com.dolphin.domain.EntrustOrder;
import com.dolphin.enums.OrderDirection;
import com.dolphin.mapper.EntrustOrderMapper;
import com.dolphin.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class DataLoaderCmdRunner implements CommandLineRunner {
    @Autowired
    private EntrustOrderMapper entrustOrderMapper;

    @Autowired
    private DisruptorTemplate disruptorTemplate;


    @Override
    public void run(String... args) throws Exception {
        List<EntrustOrder> entrustOrders = entrustOrderMapper.selectList(new LambdaQueryWrapper<EntrustOrder>()
                .eq(EntrustOrder::getStatus, 0)
                .orderByAsc(EntrustOrder::getCreated)
        );
        //处理为空的情况
        if (CollectionUtils.isEmpty(entrustOrders)){
            return;
        }
        for (EntrustOrder entrustOrder : entrustOrders) {
            disruptorTemplate.onData( entrustOrder2Order(entrustOrder));
        }
    }

    /**
     * 将EntrustOrder转成Order
     * @param entrustOrder
     * @return
     */
    private Order entrustOrder2Order(EntrustOrder entrustOrder) {
        Order order = new Order();
        order.setOrderId(entrustOrder.getId().toString());

        order.setPrice(entrustOrder.getPrice());
        order.setAmount(entrustOrder.getVolume().subtract(entrustOrder.getDeal())); // 交易的数量= 总数量- 已经成交的数量

        order.setSymbol(entrustOrder.getSymbol());
        order.setOrderDirection(OrderDirection.getOrderDirection(entrustOrder.getType().intValue()));
        order.setTime(entrustOrder.getCreated().getTime());

        return order ;
    }
}
