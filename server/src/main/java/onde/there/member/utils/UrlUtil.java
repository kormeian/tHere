package onde.there.member.utils;

import onde.there.dto.member.AuthDto;
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

    public String makeOldMemberUrl(OAuth2User oAuth2User, AuthDto.TokenResponse tokenResponse) {
        return makeRedirectUrl(addQueryParamForOldMember(makeBaseUriComponentsBuilder(oAuth2User), tokenResponse));
    }

    private String makeRedirectUrl(UriComponentsBuilder uriComponentsBuilder) {
        return uriComponentsBuilder.build().encode(StandardCharsets.UTF_8).toUriString();
    }

    private UriComponentsBuilder makeBaseUriComponentsBuilder(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String profileImageUrl = oAuth2User.getAttribute("picture");

        return UriComponentsBuilder.fromUriString("http://ec2-13-124-225-159.ap-northeast-2.compute.amazonaws.com/oauth2/redirect")
                .queryParam("name", name)
                .queryParam("email", email)
                .queryParam("picture", profileImageUrl);
    }

    private UriComponentsBuilder addQueryParamForOldMember(UriComponentsBuilder uriComponentsBuilder, AuthDto.TokenResponse tokenResponse) {
        return uriComponentsBuilder.queryParam("accessToken", tokenResponse.getAccessToken())
                .queryParam("refreshToken", tokenResponse.getRefreshToken())
                .queryParam("expirationTime",tokenResponse.getRefreshTokenExpirationTime())
                .queryParam("newMember", false);
    }
}
