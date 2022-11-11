package onde.there.member;

import onde.there.domain.Member;
import onde.there.dto.member.MemberDto;
import onde.there.image.service.AwsS3Service;
import onde.there.member.exception.MemberException;
import onde.there.member.exception.type.MemberErrorCode;
import onde.there.member.repository.MemberRepository;
import onde.there.member.service.MemberService;
import onde.there.member.utils.MailService;
import onde.there.member.utils.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    RedisService<Member> memberRedisService;

    @Mock
    MailService mailService;

    @Mock
    AwsS3Service awsS3Service;

    @Test
    void 아이디_중복_체크_성공_사용_가능한_아이디 () {
        // given
        MemberDto.CheckIdRequest request = generateCheckIdRequest();
        given(memberRepository.existsById(request.getId())).willReturn(false);
        // when
        boolean result = memberService.checkId(request);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void 아이디_중복_체크_성공_사용_불가능한_아이디 () {
        // given
        MemberDto.CheckIdRequest request = generateCheckIdRequest();
        given(memberRepository.existsById(request.getId())).willReturn(true);
        // when
        boolean result = memberService.checkId(request);
        // then
        assertThat(result).isFalse();
    }

    @Test
    void 이메일_중복_체크_성공_사용_가능한_이메일 () {
        // given
        MemberDto.CheckEmailRequest request = generateCheckEmailRequest();
        given(memberRepository.existsByEmail(request.getEmail())).willReturn(false);
        // when
        boolean result = memberService.checkEmail(request);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void 이메일_중복_체크_성공_사용_불가능한_이메일 () {
        // given
        MemberDto.CheckEmailRequest request = generateCheckEmailRequest();
        given(memberRepository.existsByEmail(request.getEmail())).willReturn(true);
        // when
        boolean result = memberService.checkEmail(request);
        // then
        assertThat(result).isFalse();
    }

    @Test
    void 닉네임_중복_체크_성공_사용_가능한_닉네임 () {
        // given
        String nickName = "test";
        given(memberRepository.existsByNickName(nickName)).willReturn(true);
        // when
        boolean result = memberService.checkNickName(nickName);
        // then
        assertThat(result).isFalse();
    }

    @Test
    void 닉네임_중복_체크_성공_사용_불가능한_닉네임 () {
        // given
        String nickName = "test";
        given(memberRepository.existsByNickName(nickName)).willReturn(false);
        // when
        boolean result = memberService.checkNickName(nickName);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void 회원가입_요청_성공 () {
        // given
        MemberDto.SignupRequest request = generateSignupRequest();
        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(memberRepository.existsById(any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn("test");
        // when
        Member member = memberService.sendSignupMail(request);
        // then
        assertThat(member.getId()).isEqualTo(request.getId());
        assertThat(member.getNickName()).isEqualTo(request.getNickName());
        assertThat(member.getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    void 회원가입_요청_실패_중복된_이메일_에러 () {
        // given
        MemberDto.SignupRequest request = generateSignupRequest();
        given(memberRepository.existsByEmail(any())).willReturn(true);
        // when
        MemberException memberException = assertThrows(MemberException.class, () -> memberService.sendSignupMail(request));
        // then
        assertThat(memberException.getMemberErrorCode()).isEqualTo(MemberErrorCode.DUPLICATED_MEMBER_EMAIL);
    }

    @Test
    void 회원가입_요청_실패_중복된_아이디_에러 () {
        // given
        MemberDto.SignupRequest request = generateSignupRequest();
        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(memberRepository.existsById(any())).willReturn(true);
        // when
        MemberException memberException = assertThrows(MemberException.class, () -> memberService.sendSignupMail(request));
        // then
        assertThat(memberException.getMemberErrorCode()).isEqualTo(MemberErrorCode.DUPLICATED_MEMBER_ID);
    }

    @Test
    void 회원_가입_성공 () {
        // given
        Member redisMember = generateMember();
        given(memberRedisService.get(any())).willReturn(Optional.of(redisMember));
        // when
        Member member = memberService.registerMember(any());
        // then
        assertThat(member.getId()).isEqualTo(redisMember.getId());
        assertThat(member.getEmail()).isEqualTo(redisMember.getEmail());
        assertThat(member.getNickName()).isEqualTo(redisMember.getNickName());
    }

    @Test
    void 회원_가입_실패_타임아웃 () {
        // given
        given(memberRedisService.get(any())).willReturn(Optional.empty());
        // when
        MemberException memberException = assertThrows(MemberException.class, () -> memberService.registerMember(any()));
        // then
        assertThat(memberException.getMemberErrorCode()).isEqualTo(MemberErrorCode.SIGNUP_CONFIRM_TIMEOUT);
    }

    @Test
    void 회원_정보_수정_성공_프로필_이미지_비밀번호_같이_변경 () {
        // given
        byte[] mockBinary = {1, 2, 4, 3, 5};
        MockMultipartFile mockMultipartFile = generateMockMultipartFile(mockBinary);
        MemberDto.UpdateRequest updateRequest = generateUpdateRequestPasswordChange();
        given(memberRepository.findById(any())).willReturn(Optional.of(generateMember()));
        given(passwordEncoder.encode(any())).willReturn("encoded");
        given(awsS3Service.uploadFiles(any())).willReturn(List.of("test"));
        // when
        Member updatedMember = memberService.update(mockMultipartFile, updateRequest);
        // then
        assertThat(updatedMember.getPassword()).isEqualTo("encoded");
        assertThat(updatedMember.getProfileImageUrl()).isEqualTo("test");
        assertThat(updatedMember.getEmail()).isEqualTo(updateRequest.getEmail());
        assertThat(updatedMember.getNickName()).isEqualTo(updateRequest.getNickName());
        assertThat(updatedMember.getId()).isEqualTo(updateRequest.getId());
    }

    @Test
    void 회원_정보_수정_성공_프로필_이미지_변경_비밀번호_미변경 () {
        // given
        byte[] mockBinary = {1, 2, 4, 3, 5};
        MockMultipartFile mockMultipartFile = generateMockMultipartFile(mockBinary);
        MemberDto.UpdateRequest updateRequest = generateUpdateRequestPasswordEmpty();
        Member member = generateMember();
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(awsS3Service.uploadFiles(any())).willReturn(List.of("test"));
        // when
        Member updatedMember = memberService.update(mockMultipartFile, updateRequest);
        // then
        assertThat(updatedMember.getPassword()).isEqualTo(member.getPassword());
        assertThat(updatedMember.getProfileImageUrl()).isEqualTo("test");
        assertThat(updatedMember.getEmail()).isEqualTo(updateRequest.getEmail());
        assertThat(updatedMember.getNickName()).isEqualTo(updateRequest.getNickName());
        assertThat(updatedMember.getId()).isEqualTo(updateRequest.getId());
    }

    @Test
    void 회원_정보_수정_성공_프로필_이미지_미변경_비밀번호_변경 () {
        // given
        byte[] mockBinary = new byte[0];
        MockMultipartFile mockMultipartFile = generateMockMultipartFile(mockBinary);
        MemberDto.UpdateRequest updateRequest = generateUpdateRequestPasswordChange();
        Member member = generateMember();
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(passwordEncoder.encode(any())).willReturn("encoded");
        // when
        Member updatedMember = memberService.update(mockMultipartFile, updateRequest);
        // then
        assertThat(updatedMember.getPassword()).isEqualTo("encoded");
        assertThat(updatedMember.getProfileImageUrl()).isEqualTo(member.getProfileImageUrl());
        assertThat(updatedMember.getEmail()).isEqualTo(updateRequest.getEmail());
        assertThat(updatedMember.getNickName()).isEqualTo(updateRequest.getNickName());
        assertThat(updatedMember.getId()).isEqualTo(updateRequest.getId());
    }

    @Test
    void 회원_정보_수정_성공_프로필_이미지_미변경_비밀번호_미변경 () {
        // given
        byte[] mockBinary = new byte[0];
        MockMultipartFile mockMultipartFile = generateMockMultipartFile(mockBinary);
        MemberDto.UpdateRequest updateRequest = generateUpdateRequestPasswordEmpty();
        Member member = generateMember();
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        // when
        Member updatedMember = memberService.update(mockMultipartFile, updateRequest);
        // then
        assertThat(updatedMember.getPassword()).isEqualTo(member.getPassword());
        assertThat(updatedMember.getProfileImageUrl()).isEqualTo(member.getProfileImageUrl());
        assertThat(updatedMember.getEmail()).isEqualTo(updateRequest.getEmail());
        assertThat(updatedMember.getNickName()).isEqualTo(updateRequest.getNickName());
        assertThat(updatedMember.getId()).isEqualTo(updateRequest.getId());
    }

    @Test
    void 회원_정보_수정_실패_찾을수_없는_회원 () {
        // given
        byte[] mockBinary = new byte[0];
        MockMultipartFile mockMultipartFile = generateMockMultipartFile(mockBinary);
        MemberDto.UpdateRequest updateRequest = generateUpdateRequestPasswordEmpty();
        Member member = generateMember();
        given(memberRepository.findById(any())).willReturn(Optional.empty());
        // when
        MemberException memberException = assertThrows(MemberException.class, () -> memberService.update(mockMultipartFile, updateRequest));
        // then
        assertThat(memberException.getMemberErrorCode()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
    }

    private MockMultipartFile generateMockMultipartFile(byte[] mockBinary) {
        return new MockMultipartFile("image", "test.png", "image/png", mockBinary);
    }

    private Member generateMember() {
        return Member.builder().id("test")
                               .password("test")
                               .profileImageUrl("test")
                               .name("test")
                               .nickName("test")
                               .build();
    }

    private MemberDto.CheckEmailRequest generateCheckEmailRequest() {
        return MemberDto.CheckEmailRequest.builder().email("test").build();
    }

    private MemberDto.CheckIdRequest generateCheckIdRequest() {
        return MemberDto.CheckIdRequest.builder().id("test").build();
    }

    private MemberDto.SignupRequest generateSignupRequest() {
        return MemberDto.SignupRequest.builder()
                                      .id("test")
                                      .nickName("test")
                                      .email("test@test.com")
                                      .password("test")
                                      .name("test")
                                      .build();
    }

    private MemberDto.UpdateRequest generateUpdateRequestPasswordChange() {
        return MemberDto.UpdateRequest.builder()
                                       .id("test")
                                       .nickName("test")
                                       .email("test@test.com")
                                       .password("test")
                                       .name("test")
                                       .build();
    }

    private MemberDto.UpdateRequest generateUpdateRequestPasswordEmpty() {
        return MemberDto.UpdateRequest.builder()
                .id("test")
                .nickName("test")
                .email("test@test.com")
                .password("")
                .name("test")
                .build();
    }
}
