package onde.there.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import onde.there.dto.member.MemberDto;
import onde.there.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "아이디 중복 확인", description = "아이디 중복 확인 결과를 반환 합니다.")
    @PostMapping("/check/id")
    public ResponseEntity<?> checkId(@Validated @RequestBody MemberDto.CheckIdRequest checkIdRequest) {
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
        memberService.sendSignupMail(signupRequest);
        MemberDto.SignupResponse response = new MemberDto.SignupResponse("인증 메일을 보냈습니다",
                                                                                 signupRequest.getEmail());
        return ResponseEntity.ok(response);
    }
}
