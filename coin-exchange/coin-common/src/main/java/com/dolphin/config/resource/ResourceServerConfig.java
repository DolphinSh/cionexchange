package com.dolphin.config.resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    /**
     * 配置拦截与放行
     *
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .sessionManagement().disable()
                .authorizeRequests()
                .antMatchers(
                        "/users/setPassword",
                        "/sms/sendTo",
                        "/users/register",
                        "/gt/register",
                        "/login",
                        "/v2/api-docs",
                        "/swagger-resources/configuration/ui",//用来获取支持的动作
                        "/swagger-resources",//用来获取api-docs的URI
                        "/swagger-resources/configuration/security",//安全选项
                        "/webjars/**",
                        "/swagger-ui.html"
                ).permitAll()
                .antMatchers("/**").authenticated()
                .and().headers().cacheControl();
        super.configure(http);
    }

    /**
     * 设置公钥
     *
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(jwtTokenStore());
        super.configure(resources);
    }

    private TokenStore jwtTokenStore() {
        JwtTokenStore jwtTokenStore = new JwtTokenStore(accessTokenConverter());
        return jwtTokenStore;
    }

    /**
     * 放在ioc容器钟
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        //resource 验证token（公钥） authorization 产生token（私钥）
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        String s = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource("coinexchange.pub");
            byte[] bytes = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
            s = new String(bytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        jwtAccessTokenConverter.setVerifierKey(s);
        return jwtAccessTokenConverter;
    }
}
