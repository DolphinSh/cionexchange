package com.dolphin.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * DisruptorHandler 的异常处理
 */
@Slf4j
public class DisruptorHandlerException implements ExceptionHandler{


    @Override
    public void handleEventException(Throwable throwable, long sequence, Object event) {
        log.info("process data error sequence ==[{}] event==[{}] ,ex ==[{}]",throwable.getMessage(),sequence,event);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        log.info("start disruptor error ==[{}]!",ex.getMessage());
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        log.info("shutdown disruptor error ==[{}]!",ex.getMessage());
    }
}
