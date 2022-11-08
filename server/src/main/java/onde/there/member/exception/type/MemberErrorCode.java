package onde.there.member.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode {
    BAD_REQUEST("입력 값을 확인해 주세요!"),
    MEMBER_NOT_FOUND("등록 되지 않은 아이디 입니다"),
    PASSWORD_MISMATCH("비밀 번호가 일치하지 않습니다"),
    DUPLICATED_MEMBER_EMAIL("중복된 이메일 입니다!"),
    DUPLICATED_MEMBER_ID("중복된 아이디 입니다!"),
    INVALID_ACCESS_TOKEN("정당하지 않은 토큰입니다"),
    INVALID_REFRESH_TOKEN("정당하지 않은 토큰입니다"),
    EXPIRED_ACCESS_TOKEN("만료된 토큰 입니다"),
    EXPIRED_REFRESH_TOKEN("만료된 토큰 입니다"),
    UNSUPPORTED_TOKEN("지원하지 않는 토큰 입니다"),
    TOKEN_CLAIMS_EMPTY("토큰 claims가 비어있습니다"),
    AUTHORIZATION_HEADER_NOT_EMPTY("인증 헤더 값이 비어있습니다"),
    AUTHORITY_ERROR("권한 없는 유저 입니다"),
    SIGNUP_CONFIRM_TIMEOUT("인증 시간이 만료되었습니다 다시 요청 해 주세요!");
    private final String description;
}
