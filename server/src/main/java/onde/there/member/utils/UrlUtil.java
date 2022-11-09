package onde.there.member.utils;

import onde.there.dto.member.MemberDto;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Component
public class UrlUtil {
    
    public String makeNewMemberUrl(OAuth2User oAuth2User) {
        return makeRedirectUrl(makeBaseUriComponentsBuilder(oAuth2User)
                .queryParam("newMember", true));
    }

    public String makeOldMemberUrl(OAuth2User oAuth2User, MemberDto.SigninResponse signinResponse) {
        return makeRedirectUrl(addQueryParamForOldMember(makeBaseUriComponentsBuilder(oAuth2User), signinResponse));
    }

    private String makeRedirectUrl(UriComponentsBuilder uriComponentsBuilder) {
        return uriComponentsBuilder.build().encode(StandardCharsets.UTF_8).toUriString();
    }

    private UriComponentsBuilder makeBaseUriComponentsBuilder(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String profileImageUrl = oAuth2User.getAttribute("picture");

        return UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("name", name)
                .queryParam("email", email)
                .queryParam("picture", profileImageUrl);
    }

    private UriComponentsBuilder addQueryParamForOldMember(UriComponentsBuilder uriComponentsBuilder, MemberDto.SigninResponse signinResponse) {
        return uriComponentsBuilder.queryParam("accessToken", signinResponse.getAccessToken())
                .queryParam("refreshToken", signinResponse.getRefreshToken())
                .queryParam("expirationTime",signinResponse.getRefreshTokenExpirationTime())
                .queryParam("newMember", false);
    }
}
