package com.dolphin.rocket;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

/**
 * 用于绑定队列
 */
public interface Sink {

    @Input("order_in")
    public MessageChannel messageChannel();
}
