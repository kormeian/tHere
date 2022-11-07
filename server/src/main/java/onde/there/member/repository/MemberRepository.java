package onde.there.member.repository;

import java.util.Optional;
import onde.there.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

	boolean existsByEmail(String email);
	boolean existsByNickName(String nickName);
	Optional<Member> findByEmail(String memberEmail);
}
