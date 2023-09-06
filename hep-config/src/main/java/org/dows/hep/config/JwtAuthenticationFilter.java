package org.dows.hep.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dows.account.util.JwtUtil;
import org.dows.framework.api.exceptions.JwtException;
import org.dows.hep.api.enums.EnumToken;
import org.dows.hep.biz.util.StatefulJwtUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * @author jx
 * @date 2023/9/6 18:08
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
            if (!token.equals(tokens.get(map.get("accountId").toString()))) {
                throw new JwtException("jwt already expire");
            }
            filterChain.doFilter(request, response);
        }
        filterChain.doFilter(request, response);
    }
}
