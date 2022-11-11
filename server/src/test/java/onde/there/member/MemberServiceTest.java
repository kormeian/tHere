package onde.there.member;

import onde.there.domain.Member;
import onde.there.dto.member.MemberDto;
import onde.there.image.service.AwsS3Service;
import onde.there.member.repository.MemberRepository;
import onde.there.member.service.MemberService;
import onde.there.member.utils.MailService;
import onde.there.member.utils.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    MailService mailService;

    @Mock
    RedisService<Member> memberRedisService;

    @Mock
    AwsS3Service awsS3Service;

    @Test
    void 아이디_중복_체크_성공_사용_가능한_이메일 () {
        // given
        MemberDto.CheckEmailRequest request = generateCheckEmailRequest();
        given(memberRepository.existsByEmail(request.getEmail())).willReturn(false);
        // when
        boolean result = memberService.checkEmail(request);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void 아이디_중복_체크_성공_사용_불가능한_이메일 () {
        // given
        MemberDto.CheckEmailRequest request = generateCheckEmailRequest();
        given(memberRepository.existsByEmail(request.getEmail())).willReturn(true);
        // when
        boolean result = memberService.checkEmail(request);
        // then
        assertThat(result).isFalse();
    }

    private MemberDto.CheckEmailRequest generateCheckEmailRequest() {
        return MemberDto.CheckEmailRequest.builder().email("test").build();
    }
}
