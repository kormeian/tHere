package onde.there.member.security.oauth2;

import lombok.RequiredArgsConstructor;
import onde.there.domain.Member;
import onde.there.dto.member.AuthDto;
import onde.there.member.repository.MemberRepository;
import onde.there.member.security.jwt.JwtService;
import onde.there.member.utils.RedisService;
import onde.there.member.utils.UrlUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RedisService<String> tokenRedisService;
    private final UrlUtil urlUtil;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        Member findedMember = memberRepository.findByEmail(email).orElse(null);
        String url = findedMember == null ? processNewMember(oAuth2User) : processOldMember(oAuth2User, findedMember);
        getRedirectStrategy().sendRedirect(request, response, url);
    }
    
    private String processNewMember(OAuth2User oAuth2User) {
        return urlUtil.makeNewMemberUrl(oAuth2User);
    }

    public String processOldMember(OAuth2User oAuth2User, Member findedMember) {
        AuthDto.TokenResponse tokenResponse = jwtService.generateToken(findedMember);
        tokenRedisService.set("RT:"+ findedMember.getId(), tokenResponse.getRefreshToken(), tokenResponse.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
        return urlUtil.makeOldMemberUrl(oAuth2User, tokenResponse);
    }
}
