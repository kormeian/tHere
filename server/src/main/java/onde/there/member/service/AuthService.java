package onde.there.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.domain.Member;
import onde.there.dto.member.MemberDto;
import onde.there.member.exception.MemberException;
import onde.there.member.exception.type.MemberErrorCode;
import onde.there.member.repository.MemberRepository;
import onde.there.member.security.jwt.JwtService;
import onde.there.member.type.TokenType;
import onde.there.member.utils.RedisService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final RedisService<String> tokenRedisService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtService jwtService;

    public MemberDto.SigninResponse signin(MemberDto.SigninRequest signinRequest) {
        memberRepository.findById(signinRequest.getId())
                .orElseThrow(() -> {
                    MemberException memberException = new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
                    log.error("memberService.signin Error");
                    log.error("request => {}", signinRequest);
                    log.error("exception => {}", memberException.toString());
                    return memberException;
                });

        UsernamePasswordAuthenticationToken authenticationToken = signinRequest.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        MemberDto.SigninResponse signinResponse = jwtService.generateToken(authentication);
        setRefreshToken(authentication, signinResponse.getRefreshToken(), signinResponse.getRefreshTokenExpirationTime());

        return signinResponse;
    }

    public MemberDto.AuthResponse auth(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    MemberException memberException = new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
                    log.error("memberService.auth Error");
                    log.error("request id => {}", memberId);
                    log.error("exception => {}", memberException.toString());
                    return memberException;
                });

        return MemberDto.AuthResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .nickName(member.getNickName())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }

    public MemberDto.SigninResponse reissue(MemberDto.ReissueRequest request) {
        jwtService.validateToken(request.getRefreshToken(), TokenType.REFRESH);
        Authentication authentication = jwtService.getAuthentication(request.getAccessToken());

        String refreshToken = tokenRedisService.get("RT:"+authentication.getName())
                .orElseThrow(() -> {
                    MemberException memberException = new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN);
                    log.error("memberService.reissue Error");
                    log.error("request => {}", request);
                    log.error("exception => {}", memberException.toString());
                    return memberException;
                });

        if (!refreshToken.equals(request.getRefreshToken())) {
            throw new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }

        MemberDto.SigninResponse signinResponse = jwtService.generateToken(authentication);
        setRefreshToken(authentication, signinResponse.getRefreshToken(), signinResponse.getRefreshTokenExpirationTime());

        return signinResponse;
    }

    private void setRefreshToken(Authentication authentication, String refreshToken,long expirationTime) {
        tokenRedisService.set("RT:"+ authentication.getName(), refreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    private void deleteRefreshToken(Authentication authentication, String refreshToken) {
        String refreshTokenKey = "RT:" + authentication.getName();
        if(tokenRedisService.get(refreshTokenKey).isPresent()) {
            tokenRedisService.delete(refreshTokenKey);
        }
    }

    public void signout(MemberDto.SignoutRequest signinRequest) {
        jwtService.validateToken(signinRequest.getAccessToken(), TokenType.ACCESS);
        jwtService.validateToken(signinRequest.getAccessToken(), TokenType.REFRESH);
        Authentication authentication = jwtService.getAuthentication(signinRequest.getAccessToken());
        deleteRefreshToken(authentication, signinRequest.getRefreshToken());
        // 엑세스 토큰 로그아웃 등록
        Long expiration = jwtService.getExpiration(signinRequest.getAccessToken());
        tokenRedisService.set(signinRequest.getRefreshToken(), "logout", expiration, TimeUnit.MILLISECONDS);
    }
}
