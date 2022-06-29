package com.dolphin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.model.R;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dolphin.mapper.NoticeMapper;
import com.dolphin.domain.Notice;
import com.dolphin.service.NoticeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService{

    @Autowired
    private NoticeService noticeService;

    /**
     * 条件分页查询
     *
     * @param page      分页参数
     * @param title     公告的标签
     * @param startTime 公告创建的开始时间
     * @param endTime   公告创建的结束时间
     * @param status    公告当前的状态
     * @return
     */
    @Override
    public Page<Notice> findByPage(Page<Notice> page, String title, String startTime, String endTime, Integer status) {
        return page(page,new LambdaQueryWrapper<Notice>()
                .like(!StringUtils.isEmpty(title),Notice::getTitle,title)
                .between(!StringUtils.isEmpty(startTime)&&!StringUtils.isEmpty(endTime),Notice::getCreated,startTime,endTime+" 23:59:59")
                .eq(status!=null,Notice::getStatus,status)
        );
    }

    /**
     * 给用户/会员展示的
     * 查询公告
     * @param page
     * @return
     */
    @Override
    public Page<Notice> findNoticeForSimple(Page<Notice> page) {
        return page(page,new LambdaQueryWrapper<Notice>()
                .eq(Notice::getStatus,1)
                .orderByAsc(Notice::getSort)
        );
    }
}
