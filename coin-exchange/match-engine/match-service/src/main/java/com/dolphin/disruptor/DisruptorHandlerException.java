package com.dolphin.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * DisruptorHandler 的异常处理
 */
@Slf4j
public class DisruptorHandlerException implements ExceptionHandler{


    @Override
    public void handleEventException(Throwable ex, long sequence, Object event) {
        log.error("process data error sequence =={} event==[{}] ,ex ==[{}]",ex.getMessage(),sequence,event);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        log.error("start disruptor error ==[{}]!",ex.getMessage());
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        log.error("shutdown disruptor error ==[{}]!",ex.getMessage());
    }
}
