package onde.there.dto.member;

import lombok.*;

public class AuthDto {
    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse {
        private String grantType;
        private String accessToken;
        private String refreshToken;
        private Long refreshTokenExpirationTime;
    }
}
