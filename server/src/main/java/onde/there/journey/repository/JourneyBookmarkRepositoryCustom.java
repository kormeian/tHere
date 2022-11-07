package onde.there.journey.repository;

import onde.there.domain.JourneyBookmark;
import onde.there.dto.journy.JourneyBookmarkDto.JourneyBookmarkPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JourneyBookmarkRepositoryCustom {
	Page<JourneyBookmark> getBookmarkPage(String memberId, Pageable pageable);
}
