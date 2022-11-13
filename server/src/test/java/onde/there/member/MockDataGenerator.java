package onde.there.member;

import onde.there.domain.Member;
import onde.there.dto.member.MemberDto;
import org.springframework.mock.web.MockMultipartFile;

public class MockDataGenerator {
    public static MockMultipartFile generateMockMultipartFile(byte[] mockBinary) {
        return new MockMultipartFile("image", "test.png", "image/png", mockBinary);
    }

    public static Member generateMember() {
        return Member.builder().id("test")
                .password("test")
                .profileImageUrl("test")
                .name("test")
                .nickName("test")
                .build();
    }

    public static MemberDto.CheckEmailRequest generateCheckEmailRequest() {
        return MemberDto.CheckEmailRequest.builder().email("test").build();
    }

    public static MemberDto.CheckIdRequest generateCheckIdRequest() {
        return MemberDto.CheckIdRequest.builder().id("test").build();
    }

    public static MemberDto.SignupRequest generateSignupRequest() {
        return MemberDto.SignupRequest.builder()
                .id("test")
                .nickName("test")
                .email("test@test.com")
                .password("test")
                .name("test")
                .build();
    }

    public static MemberDto.UpdateRequest generateUpdateRequestPasswordChange() {
        return MemberDto.UpdateRequest.builder()
                .id("test")
                .nickName("test")
                .email("test@test.com")
                .password("test")
                .name("test")
                .build();
    }

    public static MemberDto.UpdateRequest generateUpdateRequestPasswordEmpty() {
        return MemberDto.UpdateRequest.builder()
                .id("test")
                .nickName("test")
                .email("test@test.com")
                .password("")
                .name("test")
                .build();
    }
}
