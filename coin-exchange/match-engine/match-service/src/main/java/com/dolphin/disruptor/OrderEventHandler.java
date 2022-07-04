package com.dolphin.disruptor;



import com.lmax.disruptor.EventHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class OrderEventHandler implements EventHandler<OrderEvent> {

    @Override
    public void onEvent(OrderEvent orderEvent, long sequence, boolean endOfBatch) throws Exception {
        log.info("开始接收订单事件 ======> {}",orderEvent);
        log.info("处理完成我们的订单事件===================>{}", orderEvent);
    }
}
