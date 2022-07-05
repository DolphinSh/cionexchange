package com.dolphin.rocket;


import com.dolphin.disruptor.DisruptorTemplate;
import com.dolphin.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageConsumerListener {

    @Autowired
    private DisruptorTemplate disruptorTemplate;

    @StreamListener(value = "order_in") // 与Sink中的input值一致
    public void handleMessage(Order order){
        log.info("接收到了委托单:{}",order);
        disruptorTemplate.onData(order);
    }
}
