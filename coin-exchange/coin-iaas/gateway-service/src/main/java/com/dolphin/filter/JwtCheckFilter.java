package com.dolphin.filter;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
public class JwtCheckFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${no.token.access.urls:/admin/login,/user/gt/register,/user/login,/user/users/register}")
    private Set<String> noRequireTokenUrls;

    /**
     * 实现判断用户是否携带 token ，或 token 错误的功能
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.不需要token就能访问
        if (!allowNoTokenAccess(exchange)){
            return chain.filter(exchange);
        }
        //2.获取用户的 token
        String token = getUserToken(exchange);
        //3.判断用户的token是否有效
        if (StringUtils.isEmpty(token)){
            return buildNoAuthorizationResult(exchange);
        }
        Boolean hasKey = redisTemplate.hasKey(token);
        if (hasKey!=null &&hasKey){
            return chain.filter(exchange); //token有效，直接放行
        }
        return buildNoAuthorizationResult(exchange);
    }



    /**
     * 拦截器的顺序
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 判断是否需要token
     * @param exchange
     * @return
     */
    private boolean allowNoTokenAccess(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        if (noRequireTokenUrls.contains(path)) {
            return Boolean.FALSE; //不需要token
        }
        return Boolean.TRUE;
    }

    /**
     * 获取用户token
     * @param exchange
     * @return
     */
    private String getUserToken(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return token == null ? null : token.replace("bearer ","");
    }

    /**
     * 构建没有token的响应错误
     * @return
     */
    private Mono<Void> buildNoAuthorizationResult(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().set("Content-Type","application/json");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);//响应码 401
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error","NoAuthorization");
        jsonObject.put("errorMsg","Token is Null or Error!");
        DataBuffer wrap = response.bufferFactory().wrap(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Flux.just(wrap));
    }
}
