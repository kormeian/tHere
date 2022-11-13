package onde.there.member.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@AllArgsConstructor
@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secretKey;
    private String bearerType;
    private long accessTokenExpireTime;
    private long refreshTokenExpireTime;

    public JwtConfig() {
        this.bearerType = "Bearer";
        this.accessTokenExpireTime = 24 * 60 * 60 * 1000L;
        this.refreshTokenExpireTime = 7 * 24 * 60 * 60 * 1000L;
    }
}
