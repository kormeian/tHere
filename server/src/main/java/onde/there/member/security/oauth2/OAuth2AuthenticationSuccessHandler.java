package onde.there.member.security.oauth2;

import lombok.RequiredArgsConstructor;
import onde.there.domain.Member;
import onde.there.dto.member.MemberDto;
import onde.there.member.repository.MemberRepository;
import onde.there.member.security.jwt.JwtService;
import onde.there.member.utils.RandomUtil;
import onde.there.member.utils.RedisService;
import onde.there.member.utils.UrlUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
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

        String url = null;

        if (findedMember == null) {
            url = urlUtil.makeNewMemberUrl(oAuth2User);
        } else {
            MemberDto.SigninResponse signinResponse = jwtService.generateToken(findedMember);
            tokenRedisService.set("RT:"+ findedMember.getId(), signinResponse.getRefreshToken(), signinResponse.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
            url = urlUtil.makeOldMemberUrl(oAuth2User, signinResponse);
        }

        getRedirectStrategy().sendRedirect(request, response, url);
    }
}
