package com.dolphin.boot;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dolphin.disruptor.DisruptorTemplate;
import com.dolphin.domain.EntrustOrder;
import com.dolphin.mapper.EntrustOrderMapper;
import com.dolphin.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.List;

/**
 * 初始化加载
 */
@Component
public class DataLoaderCmdRunner implements CommandLineRunner {

    @Autowired
    private EntrustOrderMapper entrustOrderMapper;

    @Autowired
    private DisruptorTemplate disruptorTemplate;

    /**
     * 项目启动完毕后会执行该方法
     * @param args
     * @throws Exception
     */
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
        StopWatch stopWatch = new StopWatch() ;
        stopWatch.start();
        for (EntrustOrder entrustOrder : entrustOrders) {
            disruptorTemplate.onData(BeanUtils.entrustOrder2Order(entrustOrder));
        }
        stopWatch.stop();
        long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();
        System.out.println("总条数:+"+entrustOrders.size()+" ,总共耗时:"+lastTaskTimeMillis+"ms");
    }

}
