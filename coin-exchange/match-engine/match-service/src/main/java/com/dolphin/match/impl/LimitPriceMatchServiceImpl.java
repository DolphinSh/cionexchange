package com.dolphin.match.impl;

import com.dolphin.match.MatchService;
import com.dolphin.match.MatchServiceFactory;
import com.dolphin.match.MatchStrategy;
import com.dolphin.model.Order;
import com.dolphin.model.OrderBooks;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LimitPriceMatchServiceImpl implements MatchService, InitializingBean {

    /**
     * 执行撮合交易
     *
     * @param orderBooks
     * @param order
     */
    @Override
    public void match(OrderBooks orderBooks, Order order) {
        log.info("开始撮合！");
        orderBooks.addOrder(order);
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        MatchServiceFactory.addMatchService(MatchStrategy.LIMIT_PRICE,this);
    }

}
