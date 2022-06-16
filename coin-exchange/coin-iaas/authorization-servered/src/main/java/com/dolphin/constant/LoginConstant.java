package com.dolphin.constant;

/**
 * 登录的常量
 */
public class LoginConstant {
    /**超级管理员的角色的Code*/
    public  static  final  String ADMIN_ROLE_CODE = "ROLE_ADMIN" ;
    /**管理员登录*/
    public static final String ADMIN_TYPE = "admin_type" ;
    /**用户/会员登录*/
    public static final String MEMBER_TYPE = "member_type" ;
    /**使用用户名查询用户*/
    public static final String QUERY_ADMIN_SQL =
            "SELECT `id` ,`username`, `password`, `status` FROM sys_user WHERE username = ? ";
    /**查询用户的角色Code 判断是否为管理员*/
    public static final String QUERY_ROLE_CODE_SQL =
            "SELECT `code` FROM sys_role LEFT JOIN sys_user_role ON sys_role.id = sys_user_role.role_id WHERE sys_user_role.user_id= ?";
    /**查询所有的权限名称*/
    public static final String QUERY_ALL_PERMISSIONS =
            "SELECT `name` FROM sys_privilege";
    /**对于我们非超级用户，我们需要先查询 role->permissionId->permission*/
    public static final String QUERY_PERMISSION_SQL =
            "SELECT `name` FROM sys_privilege LEFT JOIN sys_role_privilege ON sys_role_privilege.privilege_id = sys_privilege.id LEFT JOIN sys_user_role  ON sys_role_privilege.role_id = sys_user_role.role_id WHERE sys_user_role.user_id = ?";
    /**会员查询SQL*/
    public static final String QUERY_MEMBER_SQL =
            "SELECT `id`,`password`, `status` FROM `user` WHERE mobile = ? or email = ? ";
}
