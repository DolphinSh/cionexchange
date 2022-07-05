package com.dolphin.disruptor;


import lombok.Data;

import java.io.Serializable;

@Data
public class OrderEvent implements Serializable {

    private static final long serialVersionUID = 5516075349620653480L;
    /** 时间戳 */
    private final long timestamp;
    /** 事件携带的对象 */
    protected transient Object source;

    public OrderEvent(Object source) {
        timestamp = System.currentTimeMillis();
        this.source = source;
    }

    public OrderEvent() {
        timestamp = System.currentTimeMillis();
    }
}
