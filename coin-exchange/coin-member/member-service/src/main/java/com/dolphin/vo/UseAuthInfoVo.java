package com.dolphin.vo;


import com.dolphin.domain.User;
import com.dolphin.domain.UserAuthAuditRecord;
import com.dolphin.domain.UserAuthInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(value = "用户认证的详细信息")
public class UseAuthInfoVo implements Serializable {
    @ApiModelProperty(value = "用户")
    private User user ;

    @ApiModelProperty(value = "用户认证的详情列表")
    private List<UserAuthInfo> userAuthInfoList ;

    @ApiModelProperty(value = "用户审核历史")
    private List<UserAuthAuditRecord> authAuditRecordList ;
}
