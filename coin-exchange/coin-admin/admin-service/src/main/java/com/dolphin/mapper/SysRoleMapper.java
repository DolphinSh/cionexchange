package com.dolphin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dolphin.domain.SysRole;
import org.apache.ibatis.annotations.Param;

public interface SysRoleMapper extends BaseMapper<SysRole> {
     /**
      * 获取用户角色Code的实现
      * @param userId
      * @return
      */
     String getUserRoleCode(Long userId);
}