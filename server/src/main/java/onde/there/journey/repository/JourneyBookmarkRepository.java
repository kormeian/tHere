package onde.there.journey.repository;

import java.util.List;
import java.util.Optional;
import onde.there.domain.JourneyBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JourneyBookmarkRepository extends JpaRepository<JourneyBookmark, Long> {
	List<JourneyBookmark> findAllByMemberId(String id);
	boolean existsByMemberIdAndJourneyId(String memberId, Long journeyId);

	Optional<JourneyBookmark> findByMemberIdAndJourneyId(String memberId, Long journeyId);
	boolean existsByMemberId(String id);
}
