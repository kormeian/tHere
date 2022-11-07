package onde.there.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.domain.Member;
import onde.there.dto.member.MemberDto;
import onde.there.member.exception.MemberException;
import onde.there.member.exception.type.MemberErrorCode;
import onde.there.member.security.jwt.TokenMemberId;
import onde.there.member.service.AuthService;
import onde.there.member.service.MemberService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "아이디 중복 확인", description = "아이디 중복 확인 결과를 반환 합니다.")
    @PostMapping("/check/id")
    public ResponseEntity<?> checkId(@Validated @RequestBody MemberDto.CheckIdRequest checkIdRequest) {
        log.info("checkId request => {}", checkIdRequest);
        MemberDto.CheckIdResponse response = new MemberDto.CheckIdResponse(memberService.checkId(checkIdRequest));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이메일 중복 확인", description = "이메일 중복 확인 결과를 반환 합니다.")
    @PostMapping("/check/email")
    public ResponseEntity<?> checkEmail(@Validated @RequestBody MemberDto.CheckEmailRequest checkEmailRequest) {
        MemberDto.CheckEmailResponse response = new MemberDto.CheckEmailResponse(memberService.checkEmail(checkEmailRequest));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 가입 신청", description = "회원 가입 정보를 받아서 인증 메일을 보내줍니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Validated @RequestBody MemberDto.SignupRequest signupRequest) {
        log.info("signup request => {}", signupRequest);
        Member member = memberService.sendSignupMail(signupRequest);
        MemberDto.SignupResponse response = new MemberDto.SignupResponse("인증 메일을 보냈습니다", member.getEmail());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 가입 인증", description = "인증 메일 링크를 통해 회원가입을 완료한다.")
    @GetMapping("/signup/confirm")
    public ResponseEntity<?> createConfirm(@RequestParam("key") String key) {
        log.info("createConfirm key => {}", key);
        Member member = memberService.registerMember(key);
        return ResponseEntity.ok(member.getId() + "님의 회원가입이 완료 되었습니다! 로그인 후 서비스를 사용하실 수 있습니다.");
    }

    @PatchMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(@Validated @RequestPart MultipartFile multipartFile,
                                    @Validated @RequestPart MemberDto.UpdateRequest updateRequest,
                                    @TokenMemberId String memberId) {
        if (memberId == null) {
            throw new MemberException(MemberErrorCode.AUTHORIZATION_HEADER_NOT_EMPTY);
        }

        log.info("member update request => {}", updateRequest);

        if (!updateRequest.getId().equals(memberId)) {
            throw new MemberException(MemberErrorCode.AUTHORITY_ERROR);
        }

        String password = updateRequest.getPassword().trim();
        Matcher matcher = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[0-9a-zA-Z!@#$%^&*]{10,20}$")
                .matcher(password);

        if (!password.equals("") && !matcher.matches()){
            throw new MemberException(MemberErrorCode.BAD_REQUEST);
        }

        Member member = memberService.update(multipartFile, updateRequest);

        return ResponseEntity.ok(MemberDto.AuthResponse.builder()
                        .id(member.getId())
                        .name(member.getName())
                        .email(member.getEmail())
                        .profileImageUrl(member.getProfileImageUrl())
                        .build());
    }
}
