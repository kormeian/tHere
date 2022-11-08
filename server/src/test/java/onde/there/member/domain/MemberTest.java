package onde.there.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import onde.there.domain.Member;
import onde.there.dto.member.MemberDto;
import org.junit.jupiter.api.Test;

public class MemberTest {

    @Test
    void 생성테스트_FROM_SignupRequest () {
        // given
        MemberDto.SignupRequest request = new MemberDto.SignupRequest("test", "test@test.com", "test", "test","1234");
        String encodedPassword = "인코딩 되었다";
        // when
        Member member = Member.from(request, encodedPassword);
        // then
        assertThat(member.getId()).isEqualTo(request.getId());
        assertThat(member.getEmail()).isEqualTo(request.getEmail());
        assertThat(member.getName()).isEqualTo(request.getName());
        assertThat(member.getPassword()).isEqualTo(encodedPassword);
        assertThat(member.getNickName()).isEqualTo(request.getNickName());
    }
}
