package onde.there.member;

import io.jsonwebtoken.Jwt;
import onde.there.domain.Member;
import onde.there.dto.member.AuthDto;
import onde.there.member.exception.MemberException;
import onde.there.member.exception.type.MemberErrorCode;
import onde.there.member.security.jwt.JwtService;
import onde.there.member.type.TokenType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static onde.there.member.MockDataGenerator.generateMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
public class JwtServiceTest {

    JwtService jwtService = new JwtService("test");

    @WithMockUser
    @Test
    void 토큰생성_Authentication_성공 () {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // when
        AuthDto.TokenResponse response = jwtService.generateToken(authentication);
        // then
        Authentication authenticationFromToken = jwtService.getAuthentication(response.getAccessToken());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDetails userDetailsFromToken = (UserDetails) authenticationFromToken.getPrincipal();

        assertThat(userDetails.getUsername()).isEqualTo(userDetailsFromToken.getUsername());
    }

    @WithMockUser
    @Test
    void 토큰생성_Member_성공 () {
        // given
        Member member = generateMember();
        // when
        AuthDto.TokenResponse response = jwtService.generateToken(member);
        // then
        Authentication authenticationFromToken = jwtService.getAuthentication(response.getAccessToken());
        UserDetails userDetails = member;
        UserDetails userDetailsFromToken = (UserDetails) authenticationFromToken.getPrincipal();

        assertThat(userDetails.getUsername()).isEqualTo(userDetailsFromToken.getUsername());
    }

    @WithMockUser
    @Test
    void 토큰_변조_에러_ACCESS () {
        // given
        Member member = generateMember();
        AuthDto.TokenResponse response = jwtService.generateToken(member);
        String accessToken = response.getAccessToken();
        accessToken += "tswetasdt";
        final String changedAccessToken = accessToken;
        // when
        MemberException memberException = assertThrows(MemberException.class, () -> jwtService.validateToken(changedAccessToken, TokenType.ACCESS));
        // then
        assertThat(memberException.getMemberErrorCode()).isEqualTo(MemberErrorCode.INVALID_ACCESS_TOKEN);
    }

    @WithMockUser
    @Test
    void 토큰_변조_에러_Refresh () {
        // given
        Member member = generateMember();
        AuthDto.TokenResponse response = jwtService.generateToken(member);
        String refreshToken = response.getRefreshToken();
        refreshToken += "tswetasdt";
        final String changedRefreshToken = refreshToken;
        // when
        MemberException memberException = assertThrows(MemberException.class, () -> jwtService.validateToken(changedRefreshToken, TokenType.REFRESH));
        // then
        assertThat(memberException.getMemberErrorCode()).isEqualTo(MemberErrorCode.INVALID_REFRESH_TOKEN);
    }
}
