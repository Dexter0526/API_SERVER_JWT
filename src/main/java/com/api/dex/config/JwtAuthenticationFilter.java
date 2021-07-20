package com.api.dex.config;

import com.api.dex.domain.Member;
import com.api.dex.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private long tokenValidTime = 30 * 60 * 1000L;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 헤더에서 JWT 를 받아옵니다.
//        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        Map<String, String> tokenMap = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        String token = tokenMap.get("X-AUTH-TOKEN");
        String refreshToken = tokenMap.get("Authorization");
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        // 유효한 토큰인지 확인합니다.
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // SecurityContext 에 Authentication 객체를 저장합니다.
//            SecurityContextHolder.getContext().setAuthentication(authentication);
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
            logger.info("doFilter authentication === " + authentication.getName());
            httpServletResponse.setHeader("X-AUTH-TOKEN", token);
        }else if(refreshToken != null && jwtTokenProvider.validateToken(refreshToken)){
            Member member = jwtTokenProvider.validateRefreshToken(refreshToken);
            if(member != null){
                String accessToken = jwtTokenProvider.createToken(member.getAccount(), member.getMemberRole(), tokenValidTime);
                // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                // SecurityContext 에 Authentication 객체를 저장합니다.
//            SecurityContextHolder.getContext().setAuthentication(authentication);
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(authentication);
                logger.info("doFilter authentication === " + authentication.getName());

                httpServletResponse.setHeader("X-AUTH-TOKEN", accessToken);
            }
        }
        chain.doFilter(request, response);
        logger.info("doFilter token === " + token);

    }
}