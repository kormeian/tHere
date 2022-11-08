package onde.there.place.repository;

import static com.querydsl.jpa.JPAExpressions.select;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import onde.there.domain.QPlace;
import onde.there.domain.QPlaceHeart;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryCustomImpl implements PlaceRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;


	@Override
	public List<Tuple> findAllByJourneyOrderByPlaceTimeAsc(Long journeyId, String memberId) {

		//given
		QPlace p = new QPlace("p");
		QPlaceHeart ph = new QPlaceHeart("ph");

		//when
		List<Tuple> tuples = jpaQueryFactory
			.select(p, select()
				.from(ph)
				.where(ph.member.id.eq(memberId).and(ph.place.eq(p))).exists()
				.as("hearted_check")).from(p)
			.where(p.journey.id.eq(journeyId))
			.orderBy(p.placeTime.asc())
			.fetch();

		return tuples;
	}
}
