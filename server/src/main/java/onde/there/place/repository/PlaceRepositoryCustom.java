package onde.there.place.repository;

import java.util.List;
import onde.there.dto.place.PlaceDto.Response;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepositoryCustom {

	List<Response> findAllByJourneyOrderByPlaceTimeAsc(Long journeyId, String memberId);
}
