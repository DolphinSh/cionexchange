package com.dolphin.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.MarketMapper;
import com.dolphin.domain.Market;
import com.dolphin.service.MarketService;
@Service
public class MarketServiceImpl extends ServiceImpl<MarketMapper, Market> implements MarketService{

}
