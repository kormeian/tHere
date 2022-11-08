package onde.there.place.repository;

import java.util.List;
import onde.there.domain.Journey;
import onde.there.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

	boolean existsById(Long placeId);
	List<Place> findAllByJourneyIdOrderByPlaceTimeAsc(Long journeyId);

	Integer deleteAllByJourneyId(Long journeyId);

	Long countAllByJourneyId(Long journeyId);
	List<Place> findAllByJourney(Journey journey);
}
