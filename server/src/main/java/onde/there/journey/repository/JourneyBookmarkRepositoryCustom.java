package onde.there.journey.repository;

import com.querydsl.core.Tuple;
import onde.there.domain.JourneyBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JourneyBookmarkRepositoryCustom {
	Page<JourneyBookmark> getBookmarkPage(String memberId, Pageable pageable);
	Tuple bookmarkConfirmation(Long journeyId, String memberId);
}
