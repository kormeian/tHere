package onde.there.member.security.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.domain.Member;
import onde.there.dto.member.AuthDto;
import onde.there.member.exception.MemberException;
import onde.there.member.exception.type.MemberErrorCode;
import onde.there.member.type.TokenType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("spring.jwt.secret")
    private String secretKey;
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME =  30 * 60 * 1000L;              // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일

    public AuthDto.TokenResponse generateToken(Authentication authentication) {
        long now = (new Date()).getTime();
        String accessToken = generateAccessToken(authentication.getName(), now);
        String refreshToken = generateRefreshToken(now);
        return buildTokenResponse(accessToken, refreshToken);
    }

    public AuthDto.TokenResponse generateToken(Member member) {
        long now = (new Date()).getTime();
        String accessToken = generateAccessToken(member.getId(), now);
        String refreshToken = generateRefreshToken(now);
        return buildTokenResponse(accessToken, refreshToken);
    }

    private String generateAccessToken(String memberId, long now) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId required not null");
        }
        // TODO 고칠 수 있을 거 같음
        return Jwts.builder()
                .setSubject(memberId)
                .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private String generateRefreshToken(long now) {
        return Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        Collection<? extends GrantedAuthority> authorities = new HashSet<>();
        UserDetails principal = Member.builder()
                                      .id(claims.getSubject())
                                      .build();
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public void validateToken(String token, TokenType tokenType) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        } catch (MalformedJwtException e) {
            log.error("지원 하지 않는 토큰");
            logToken(log, tokenType, token);
            throwMalformedJwtException(tokenType);
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰");
            logToken(log, tokenType, token);
            throwExpiredJwtException(tokenType);
        } catch (UnsupportedJwtException e) {
            log.error("지원 하지 않는 토큰");
            logToken(log, tokenType, token);
            throw new MemberException(MemberErrorCode.TOKEN_CLAIMS_EMPTY);
        } catch (JwtException e) {
            log.error("변조된 토큰");
            logToken(log, tokenType, token);
            throwJwtException(tokenType);
        }
    }

    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private void throwMalformedJwtException(TokenType tokenType) {
        switch (tokenType) {
            case ACCESS:
                throw new MemberException(MemberErrorCode.INVALID_ACCESS_TOKEN);
            case REFRESH:
                throw new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private void throwExpiredJwtException(TokenType tokenType) {
        switch (tokenType) {
            case ACCESS:
                throw new MemberException(MemberErrorCode.EXPIRED_ACCESS_TOKEN);
            case REFRESH:
                throw new MemberException(MemberErrorCode.EXPIRED_REFRESH_TOKEN);
        }
    }

    private void throwJwtException(TokenType tokenType) {
        switch (tokenType) {
            case ACCESS:
                throw new MemberException(MemberErrorCode.INVALID_ACCESS_TOKEN);
            case REFRESH:
                throw new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private void logToken(Logger log, TokenType tokenType, String token) {
        log.error("TOKEN Type => {}", tokenType.name());
        log.error("token => {}", token);
    }

    private AuthDto.TokenResponse buildTokenResponse(String accessToken, String refreshToken) {
        return AuthDto.TokenResponse.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }
}
