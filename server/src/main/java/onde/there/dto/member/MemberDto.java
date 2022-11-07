package onde.there.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import onde.there.domain.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class MemberDto {

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CheckIdRequest", description = "아이디 중복 확인 요청")
    public static class CheckIdRequest {
        @NotBlank(message = "아이디를 입력 해 주세요!")
        @Schema(description = "아이디")
        private String id;
    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckIdResponse {
        @Schema(description = "중복 확인 결과")
        boolean result;
    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CheckEmailRequest", description = "이메일 중복 확인 요청")
    public static class CheckEmailRequest {
        @Email(message = "이메일 형식으로 입력 해 주세요!")
        @NotBlank(message = "이메일 값을 입력 해 주세요!")
        @Schema(description = "이메일")
        private String email;

    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckEmailResponse {
        @Schema(description = "중복 확인 결과")
        boolean result;
    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckNickNameResponse {
        @Schema(description = "중복 확인 결과")
        boolean result;
    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "SignupRequest", description = "회원 가입 요청 요청")
    public static class SignupRequest {
        @Pattern(regexp = "^(?=.*[a-zA-Z])[0-9a-zA-Z]{4,60}$", message = "아이디: 알파벳, 숫자를 조합한 4자리~60자리 문자열을 입력 해 주세요!")
        @Schema(description = "아이디")
        private String id;

        @Email(message = "이메일 형식으로 입력 해 주세요!")
        @NotBlank(message = "이메일 값을 입력 해 주세요!")
        @Schema(description = "이메일")
        private String email;

        @Pattern(regexp = "^[가-힣]{2,5}$", message = "이름: 한글로된 2자리~5자리 문자열을 입력 해 주세요!")
        @Schema(description = "이름")
        private String name;

        @Pattern(regexp = "^[0-9a-zA-Z가-힣]{2,10}$", message = "닉네임: 알파벳,한글,숫자로된 2-10자리 문자열을 입력 해 주세요!")
        @Schema(description = "닉네임")
        private String nickName;

        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[0-9a-zA-Z!@#$%^&*]{10,20}$", message = "비밀번호 형식에 맞게 입력 해 주세요!")
        @Schema(description = "비밀번호")
        private String password;
    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupResponse {
        private String message;
        private String email;
    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupConfirmResponse {
        private String id;
        private String email;
        private String name;

        public static SignupConfirmResponse from(Member member) {
            SignupConfirmResponse response = new SignupConfirmResponse();
            response.id = member.getId();
            response.email = member.getEmail();
            response.name = member.getName();
            return response;
        }
    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "SigninRequest", description = "로그인 요청")
    public static class SigninRequest {
        @NotBlank(message = "아이디 값을 입력 해 주세요!")
        private String id;
        @NotBlank(message = "비밀번호를 입력 해 주세요!")
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(id, password);
        }
    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "signinResponse", description = "로그인 응답")
    public static class SigninResponse {
        @Schema(description = "Bearer")
        private String grantType;
        @Schema(description = "access 토큰")
        private String accessToken;
        @Schema(description = "refresh 토큰")
        private String refreshToken;
        @Schema(description = "토큰 만료 시간")
        private Long refreshTokenExpirationTime;
    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "AuthResponse", description = "인증 응답")
    public static class AuthResponse {
        @Schema(description = "아이디")
        private String id;
        @Schema(description = "이메일")
        private String email;
        @Schema(description = "이름")
        private String name;
        @Schema(description = "프로필 이미지 경로")
        private String profileImageUrl;
        @Schema(description = "닉네임")
        private String nickName;

    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReissueRequest {
        @NotBlank(message = "accessToken 을 입력해주세요.")
        private String accessToken;

        @NotBlank(message = "refreshToken 을 입력해주세요.")
        private String refreshToken;
    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignoutRequest {
        @NotBlank(message = "accessToken 을 입력해주세요.")
        private String accessToken;

        @NotBlank(message = "refreshToken 을 입력해주세요.")
        private String refreshToken;
    }

    @ToString
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "UpdateRequest", description = "회원 정보 변경 요청")
    public static class UpdateRequest {
        @Pattern(regexp = "^(?=.*[a-zA-Z])[0-9a-zA-Z]{4,60}$", message = "아이디: 알파벳, 숫자를 조합한 4자리~60자리 문자열을 입력 해 주세요!")
        @Schema(description = "아이디")
        private String id;

        @Email(message = "이메일 형식으로 입력 해 주세요!")
        @NotBlank(message = "이메일 값을 입력 해 주세요!")
        @Schema(description = "이메일")
        private String email;

        @Pattern(regexp = "^[가-힣]{2,5}$", message = "이름: 한글로된 2자리~5자리 문자열을 입력 해 주세요!")
        @Schema(description = "이름")
        private String name;

        @Pattern(regexp = "^[0-9a-zA-Z가-힣]{2,10}$", message = "닉네임: 알파벳,한글,숫자로된 2-10자리 문자열을 입력 해 주세요!")
        @Schema(description = "닉네임")
        private String nickName;

        @NotNull
        @Schema(description = "비밀번호")
        private String password;
    }
}
