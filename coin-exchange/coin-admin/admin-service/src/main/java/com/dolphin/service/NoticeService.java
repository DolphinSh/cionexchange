package com.dolphin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dolphin.domain.Notice;
import com.baomidou.mybatisplus.extension.service.IService;
public interface NoticeService extends IService<Notice>{

    /**
     * 条件分页查询
     * @param page 分页参数
     * @param title 公告的标签
     * @param startTime 公告创建的开始时间
     * @param endTime 公告创建的结束时间
     * @param status 公告当前的状态
     * @return
     */
    Page<Notice> findByPage(Page<Notice> page, String title, String startTime, String endTime, Integer status);

    /**
     * 给用户/会员展示的
     * @param page
     * @return
     */
    Page<Notice> findNoticeForSimple(Page<Notice> page);
}
