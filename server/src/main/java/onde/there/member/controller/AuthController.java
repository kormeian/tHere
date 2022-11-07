package onde.there.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.dto.member.MemberDto;
import onde.there.member.exception.MemberException;
import onde.there.member.exception.type.MemberErrorCode;
import onde.there.member.security.jwt.TokenMemberId;
import onde.there.member.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/members")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "로그인")
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Validated @RequestBody MemberDto.SigninRequest signinRequest) {
        log.info("signin request => {}", signinRequest.getId());
        MemberDto.SigninResponse response = authService.signin(signinRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth")
    public ResponseEntity<?> auth(@TokenMemberId String memberId) {
        if (memberId == null) {
            throw new MemberException(MemberErrorCode.AUTHORIZATION_HEADER_NOT_EMPTY);
        }
        log.info("auth request memberId => {}", memberId);
        return ResponseEntity.ok(authService.auth(memberId));
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@Validated @RequestBody MemberDto.ReissueRequest request) {
        log.info("reissue request => {}", request);
        return ResponseEntity.ok(authService.reissue(request));
    }
}
