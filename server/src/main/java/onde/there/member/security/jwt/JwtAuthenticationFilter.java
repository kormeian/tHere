package onde.there.member.security.jwt;


import lombok.extern.slf4j.Slf4j;
import onde.there.member.exception.MemberException;
import onde.there.member.exception.type.MemberErrorCode;
import onde.there.member.type.TokenType;
import onde.there.member.utils.RedisService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";
    private final JwtService jwtService;
    private final RedisService<String> redisService;

    public JwtAuthenticationFilter(JwtService jwtService, RedisService<String> redisService) {
        this.jwtService = jwtService;
        this.redisService = redisService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = resolveToken((HttpServletRequest) request);

        if (token != null) {
            validateToken(token);
        }

        chain.doFilter(request, response);
    }

    private void validateToken(String token) {
        jwtService.validateToken(token, TokenType.ACCESS);
        String logout = checkLogoutToken(token);
        if(logout == null) {
            Authentication authentication = jwtService.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private String checkLogoutToken(String token) {
        return redisService.get(token).orElse(null);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
