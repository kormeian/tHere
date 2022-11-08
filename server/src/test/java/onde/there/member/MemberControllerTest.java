package onde.there.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import onde.there.domain.Member;
import onde.there.dto.member.MemberDto;
import onde.there.member.repository.MemberRepository;
import onde.there.member.security.SecurityConfig;
import onde.there.member.security.jwt.JwtService;
import onde.there.member.security.oauth2.OAuth2AuthenticationSuccessHandler;
import onde.there.member.security.oauth2.Oauth2MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
//@WebMvcTest(controllers = MemberControllerTest.class
//        , includeFilters = {
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Oauth2MemberService.class),
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = OAuth2AuthenticationSuccessHandler.class),
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtService.class),
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = MemberRepository.class),
//})
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

//    @BeforeEach
    void delete_all_member() {
        System.out.println("Member Table Clear");
        memberRepository.deleteAll();
    }

    @Test
    void 아이디중복확인_성공_케이스 () throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.CheckIdRequest checkIdRequest = new MemberDto.CheckIdRequest("test2");
        String content = objectMapper.writeValueAsString(checkIdRequest);

        mockMvc.perform(post("/members/check/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['result']").exists())
                .andDo(print());
    }

    @Test
    void 아이디중복확인_실패_케이스_BADREQUEST_바디가없을경우 () throws Exception{
        String content = "";
        mockMvc.perform(post("/members/check/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("BAD_REQUEST"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

    @Test
    void 아이디중복확인_실패_케이스_BADREQUEST_id값_비어있는_경우 () throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.CheckIdRequest checkIdRequest = new MemberDto.CheckIdRequest("");
        String content = objectMapper.writeValueAsString(checkIdRequest);

        mockMvc.perform(post("/members/check/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("BAD_REQUEST"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

    @Test
    void 이메일중복확인_성공_케이스 () throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.CheckEmailRequest checkEmailRequest = new MemberDto.CheckEmailRequest("test2@test.com");
        String content = objectMapper.writeValueAsString(checkEmailRequest);

        mockMvc.perform(post("/members/check/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['result']").exists())
                .andDo(print());
    }

    @Test
    void 이메일중복확인_실패_케이스_BADREQUEST_바디가없을경우 () throws Exception{
        String content = "";
        mockMvc.perform(post("/members/check/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("BAD_REQUEST"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

    @Test
    void 이메일중복확인_실패_케이스_BADREQUEST_email값_비어있는_경우 () throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.CheckEmailRequest checkEmailRequest = new MemberDto.CheckEmailRequest("");
        String content = objectMapper.writeValueAsString(checkEmailRequest);

        mockMvc.perform(post("/members/check/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("BAD_REQUEST"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

    @Test
    void 이메일중복확인_실패_케이스_BADREQUEST_이메일_형식_아님 () throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.CheckEmailRequest checkEmailRequest = new MemberDto.CheckEmailRequest("test2");
        String content = objectMapper.writeValueAsString(checkEmailRequest);

        mockMvc.perform(post("/members/check/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("BAD_REQUEST"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

    @Test
    void 회원가입요청_성공_케이스() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.SignupRequest signupRequest = MemberDto.SignupRequest.builder()
                                                .id("test2")
                                                .email("test2@test.com")
                                                .name("테스트")
                                                .password("1234")
                                                .nickName("테스트")
                                                .build();
        String content = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(post("/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['message']").exists())
                .andExpect(jsonPath("$['email']").exists())
                .andDo(print());
    }

    @Test
    void 회원가입요청_실패_케이스_중복된_이메일() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.SignupRequest signupRequest = MemberDto.SignupRequest.builder()
                                                .id("test2")
                                                .email("test2@test.com")
                                                .name("테스트")
                                                .password("1234")
                                                .nickName("테스트닉네임")
                                                .build();
        String content = objectMapper.writeValueAsString(signupRequest);
        memberRepository.save(new Member("test2", "test2@test.com", "1234", "test2"));

        mockMvc.perform(post("/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("DUPLICATED_MEMBER_EMAIL"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

    @Test
    void 회원가입요청_실패_케이스_중복된_아이디() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.SignupRequest signupRequest = MemberDto.SignupRequest.builder()
                                                .id("test2")
                                                .email("test2@test.com")
                                                .name("테스트")
                                                .password("1234")
                                                .nickName("테스트닉네임")
                                                .build();
        String content = objectMapper.writeValueAsString(signupRequest);
        memberRepository.save(new Member("test2", "test1@test.com", "1234", "test2"));

        mockMvc.perform(post("/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("DUPLICATED_MEMBER_ID"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

}
