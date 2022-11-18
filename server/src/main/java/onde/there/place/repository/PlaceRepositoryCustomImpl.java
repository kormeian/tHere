package onde.there.place.repository;

import static com.querydsl.jpa.JPAExpressions.select;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import onde.there.domain.Place;
import onde.there.domain.QPlace;
import onde.there.domain.QPlaceHeart;
import onde.there.dto.place.PlaceDto;
import onde.there.dto.place.PlaceDto.Response;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryCustomImpl implements PlaceRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;


	@Override
	public List<Response> findAllByJourneyOrderByPlaceTimeAsc(Long journeyId, String memberId) {

		//given
		QPlace p = new QPlace("p");
		QPlaceHeart ph = new QPlaceHeart("ph");

		//when
		List<Tuple> tuples = jpaQueryFactory
			.select(p, select()
				.from(ph)
				.where(ph.member.id.eq(memberId).and(ph.place.eq(p))).exists()
				.as("hearted_check"))
			.from(p)
			.where(p.journey.id.eq(journeyId).and(p.deleted.eq(false)))
			.orderBy(p.placeTime.asc())
			.fetch();

		List<PlaceDto.Response> responses = new ArrayList<>();
		for (Tuple tuple : tuples) {
			Place place = tuple.get(0, Place.class);
			boolean heartedCheck = Boolean.TRUE.equals(tuple.get(1, Boolean.class));

			if (place != null) {
				Response response = Response.toResponse(place);
				response.setHeartedCheck(heartedCheck);
				responses.add(response);
			}
		}
		return responses;
	}
}
