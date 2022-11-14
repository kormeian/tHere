package onde.there.journey.repository;

import onde.there.domain.Journey;
import onde.there.dto.journy.JourneyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface JourneyRepositoryCustom {

	Slice<Journey> searchAll(JourneyDto.FilteringRequest filteringRequest, Pageable pageable, Long cursorId);
	Slice<Journey> journeyListByMemberId(String memberId, Pageable pageable, Long cursorId);
}
