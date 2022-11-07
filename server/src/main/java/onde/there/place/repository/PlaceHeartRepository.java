package onde.there.place.repository;

import java.util.List;
import java.util.Optional;
import onde.there.domain.Member;
import onde.there.domain.Place;
import onde.there.domain.PlaceHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceHeartRepository extends JpaRepository<PlaceHeart, Long> {

	boolean existsByPlaceIdAndMemberId(Long placeId, String memberId);

	List<PlaceHeart> findAllByPlaceId(Long placeId);

	@Query(name = "SELECT p FROM place_heart p WHERE p.place_id = : find_id ")
	Long countByPlaceId(@Param(value = "find_id") Long placeId);

	Optional<PlaceHeart> findByPlaceAndMember(Place place, Member member);
}
