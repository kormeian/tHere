package onde.there.place.repository;

import com.querydsl.core.Tuple;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepositoryCustom {

	List<Tuple> findAllByJourneyOrderByPlaceTimeAsc(Long journeyId, String memberId);
}
