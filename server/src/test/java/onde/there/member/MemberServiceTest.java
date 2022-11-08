package onde.there.member;

import static org.assertj.core.api.Assertions.assertThat;

import onde.there.domain.Member;
import onde.there.dto.member.MemberDto;
import onde.there.member.exception.MemberException;
import onde.there.member.exception.type.MemberErrorCode;
import onde.there.member.security.jwt.JwtService;
import onde.there.member.service.MemberService;
import onde.there.member.utils.MailService;
import onde.there.member.utils.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private onde.there.member.repository.MemberRepository memberRepository;

    @Autowired
    private RedisService<Member> redisService;
    @Autowired
    private RedisService<String> tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    class TestMailService extends MailService {

        public TestMailService(JavaMailSender javaMailSender) {
            super(javaMailSender);
        }

        @Override
        public void sendSignupMail(String uuid, Member member) {
            System.out.println("메일 전송 기능 잘 호출 됨");
        }
    }

    @Transactional
    @Test
    void 아이디중복_확인_성공_케이스_중복된아이디 () {
        // given
        Member member = new Member("test2", "test2", "test2", "test2");
        memberRepository.save(member);
        // when
        MemberDto.CheckIdRequest request = new MemberDto.CheckIdRequest("test2");
        boolean result = memberService.checkId(request);
        // then
        assertThat(result).isFalse();
    }

    @Transactional
    @Test
    void 아이디중복_확인_성공_케이스_사용가능아이디 () {
        // given
        Member member = new Member("test2", "test2", "test2", "test2");
        memberRepository.save(member);
        // when
        MemberDto.CheckIdRequest request = new MemberDto.CheckIdRequest("newId");
        boolean result = memberService.checkId(request);
        // then
        assertThat(result).isTrue();
    }

    @Transactional
    @Test
    void 이메일중복_확인_성공_케이스_중복된아이디 () {
        // given
        Member member = new Member("test2", "test2", "test2", "test2");
        memberRepository.save(member);
        // when
        MemberDto.CheckEmailRequest request = new MemberDto.CheckEmailRequest("test2");
        boolean result = memberService.checkEmail(request);
        // then
        assertThat(result).isFalse();
    }

    @Transactional
    @Test
    void 이메일중복_확인_성공_케이스_사용가능아이디 () {
        // given
        Member member = new Member("test2", "test2", "test2", "test2");
        memberRepository.save(member);
        // when
        MemberDto.CheckEmailRequest request = new MemberDto.CheckEmailRequest("newEmail");
        boolean result = memberService.checkEmail(request);
        // then
        assertThat(result).isTrue();
    }

    @Transactional
    @Test
    void 회원가입요청_성공 () {
        // given
        TestMailService testMailService = new TestMailService(new JavaMailSenderImpl());
        MemberDto.SignupRequest request = new MemberDto.SignupRequest("test2","test@test.com","test2","test2", "1234");

        // when
        Member member = memberService.sendSignupMail(request);
        // then
        assertThat(member.getId()).isEqualTo(request.getId());
        assertThat(member.getName()).isEqualTo(request.getName());
        assertThat(member.getEmail()).isEqualTo(request.getEmail());
        assertThat(passwordEncoder.matches("1234", member.getPassword())).isTrue();
        assertThat(member.getNickName()).isEqualTo(request.getNickName());
    }

    @Transactional
    @Test
    void 회원가입요청_실패_중복된_이메일() {
        //given
        MemberDto.SignupRequest request = new MemberDto.SignupRequest("test2","test@test.com","test2","test2", "1234");
        memberRepository.save(new Member("test2","test@test.com","1234","test2"));
        //when
        MemberException memberException = org.junit.jupiter.api.Assertions.assertThrows(MemberException.class,
                () -> memberService.sendSignupMail(request));
        //then
        assertThat(memberException.getMemberErrorCode()).isEqualTo(MemberErrorCode.DUPLICATED_MEMBER_EMAIL);
    }

    @Transactional
    @Test
    void 회원가입요청_실패_중복된_아이디() {
        //given
        MemberDto.SignupRequest request = new MemberDto.SignupRequest("test2","test@test.com","test2","test2", "1234");
        memberRepository.save(new Member("test2","asf@naver.com","1234","test2"));
        //when
        MemberException memberException = org.junit.jupiter.api.Assertions.assertThrows(MemberException.class,
                () -> memberService.sendSignupMail(request));
        //then
        assertThat(memberException.getMemberErrorCode()).isEqualTo(MemberErrorCode.DUPLICATED_MEMBER_ID);
    }

}
