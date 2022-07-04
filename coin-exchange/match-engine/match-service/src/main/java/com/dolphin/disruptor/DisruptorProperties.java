package com.dolphin.disruptor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.disruptor")
public class DisruptorProperties {

    //RingBuffer 缓冲区大小, 默认 1024 * 1024 使用2进制会更快
    private int ringBufferSize = 1024 * 1024;
    /**
     * 是否为多生产者，如果是则通过 RingBuffer.createMultiProducer 创建一个多生产 的 RingBuffer，
     * 否则通过 RingBuffer.createSingleProducer 创建一个单生产者的 RingBuffer
     */
    private boolean multiProducer = false;
}
