package org.dows.hep.config;

import cn.hutool.json.JSONObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dows.account.util.JwtUtil;
import org.dows.hep.api.enums.EnumToken;
import org.dows.hep.biz.util.StatefulJwtUtil;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * @author jx
 * @date 2023/9/7 18:08
 */
@RequiredArgsConstructor
@Order(1)
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!request.getRequestURI().endsWith("login")) {
            final Map<String, String> tokens = StatefulJwtUtil.TOKENS;
            //需要验证的
            String token = request.getHeader("token");
            Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
            //管理员可以多人登录
            if(!map.get("accountName").equals("Admin")) {
                //非管理员只能登录一个账号
                if (!token.equals(tokens.get(map.get("accountId").toString()))) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.set("code", HttpStatus.UNAUTHORIZED.value());
                    jsonObject.set("descr", HttpStatus.UNAUTHORIZED.getReasonPhrase());
                    jsonObject.set("status", false);
                    jsonObject.set("timestamp", System.currentTimeMillis());
                    jsonObject.set("path", request.getRequestURI());
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    renderString(response, jsonObject.toString());
                    return;
                }
            }
            filterChain.doFilter(request, response);
        }else {
            filterChain.doFilter(request, response);
        }
    }

    public static void renderString(HttpServletResponse response, String info) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");

        try {
            response.getWriter().print(info);
        } catch (IOException var3) {
        }
    }
}
