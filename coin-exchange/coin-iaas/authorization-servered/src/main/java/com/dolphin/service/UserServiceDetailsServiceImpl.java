package com.dolphin.service;

import com.dolphin.constant.LoginConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String loginType = requestAttributes.getRequest().getParameter("login_type");
        if (StringUtils.isEmpty(loginType)) {
            throw new AuthenticationServiceException("登录类型不能为null");
        }

        UserDetails userDetails = null;
        try {
            String grant_type = requestAttributes.getRequest().getParameter("grant_type");//refresh_token进行纠正
            if (LoginConstant.REFRESH_TYPE.equals(grant_type.toUpperCase())){
                username = adjustUsername(username,loginType);
            }
            switch (loginType) {
                case LoginConstant.ADMIN_TYPE:
                    userDetails = loadSysUserByUsername(username);
                    break;
                case LoginConstant.MEMBER_TYPE:
                    userDetails = loadMemberUserByUsername(username);
                    break;
                default:
                    throw new AuthenticationServiceException("暂不支持的登录方式");
            }
        } catch (AuthenticationServiceException e) {
            e.printStackTrace();
        }
        return userDetails;
    }

    /**
     * 纠正用户的名称
     * @param username 用户的id
     * @param loginType admin_type member_type
     * @return
     */
    private String adjustUsername(String username, String loginType) {
        if (LoginConstant.ADMIN_TYPE.equals(loginType)){
            //管理员的纠正方式
            return jdbcTemplate.queryForObject(LoginConstant.QUERY_ADMIN_USER_WITH_ID,String.class,username);
        }
        if (LoginConstant.MEMBER_TYPE.equals(loginType)) {
            //会员的纠正方式
            return jdbcTemplate.queryForObject(LoginConstant.QUERY_MEMBER_USER_WITH_ID,String.class,username);
        }
        return username;
    }


    /**
     * 后台管理人员登录
     *
     * @param username
     */
    private UserDetails loadSysUserByUsername(String username) {
        //1 使用用户名查询用户
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_ADMIN_SQL, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                if (resultSet.wasNull()) {
                    throw new UsernameNotFoundException("用户名: " + username + "不存在");
                }
                long id = resultSet.getLong("id"); //用户的id
                String password = resultSet.getString("password");
                int status = resultSet.getInt("status");
                return new User(
                        String.valueOf(id),//使用id -> username
                        password,
                        status == 1,
                        true,
                        true,
                        true,
                        getSysUserPermissions(id)
                );
            }

        }, username);
    }

    /**
     * 通过用户id查询查询用户角色
     *
     * @return
     */
    private Collection<? extends GrantedAuthority> getSysUserPermissions(Long id) {
        //1 当用户为超级管理员时，他拥有所有的权限数据
        String roleCode = jdbcTemplate.queryForObject(LoginConstant.QUERY_ROLE_CODE_SQL, String.class, id);
        List<String> permissions = null;
        if (roleCode.equals(LoginConstant.ADMIN_ROLE_CODE)) {
            permissions = jdbcTemplate.queryForList(LoginConstant.QUERY_ALL_PERMISSIONS, String.class);
        } else {
            permissions = jdbcTemplate.queryForList(LoginConstant.QUERY_PERMISSION_SQL, String.class, id);
        }
        //2 普通用户，需要使用角色-> 权限数据
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptySet();
        }
        return permissions.stream()
                .distinct()//去重
                .map(perm -> new SimpleGrantedAuthority(perm))
                .collect(Collectors.toSet());
    }

    /**
     * 会员登录
     *
     * @param username
     */
    private UserDetails loadMemberUserByUsername(String username) {
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_MEMBER_SQL, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                if (resultSet.wasNull()){
                    throw new UsernameNotFoundException("用户: "+username+"不存在");
                }
                long id = resultSet.getLong("id");//会员的id
                String password = resultSet.getString("password");//会员的登录密码
                int status = resultSet.getInt("status");//会员的状态
                return new User(
                        String.valueOf(id),
                        password,
                        status == 1,
                        true,
                        true,
                        true,
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            }
        },username,username);
    }
}
