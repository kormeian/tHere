package onde.there.journey.repository;

import static onde.there.domain.QJourney.journey;
import static onde.there.domain.QJourneyTheme.journeyTheme;
import static onde.there.domain.QMember.*;
import static onde.there.domain.type.JourneyThemeType.findByTheme;
import static onde.there.domain.type.RegionType.findByRegion;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import onde.there.domain.Journey;
import onde.there.dto.journy.JourneyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class JourneyRepositoryImpl implements JourneyRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Slice<Journey> searchAll(
		JourneyDto.FilteringRequest filteringRequest, Pageable pageable, Long cursorId) {

		BooleanBuilder filteredRegion = new BooleanBuilder();
		conRegions(filteringRequest.getRegions(), filteredRegion);

		BooleanBuilder filteredTheme = new BooleanBuilder();
		conJourneyTheme(filteringRequest.getThemes(), filteredTheme);

		List<Journey> content = jpaQueryFactory
			.selectFrom(journey)
			.innerJoin(journey.journeyThemes, journeyTheme)
			.innerJoin(journey.member, member)
			.fetchJoin()
			.where(
				journey.disclosure.eq("public"),
				filteredRegion,
				filteredTheme,
				eqTitle(filteringRequest.getKeyword()),
				eqCursorId(cursorId)
			)
			.groupBy(journey)
			.limit(pageable.getPageSize() + 1)
			.fetch();

			boolean hasNext = false;
			if (content.size() > pageable.getPageSize()) {
				content.remove(pageable.getPageSize());
				hasNext = true;
			}

//		JPAQuery<Long> countQuery = jpaQueryFactory
//			.select(journey.countDistinct())
//			.from(journey)
//			.innerJoin(journey.journeyThemes, journeyTheme)
//			.where(
//				journey.disclosure.eq("public"),
//				filteredRegion,
//				filteredTheme,
//				eqTitle(filteringRequest.getKeyword()));
//
//		return PageableExecutionUtils.getPage(content, pageable,
//			countQuery::fetchOne);

		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public Slice<Journey> journeyListByMemberId(String memberId, Pageable pageable, Long cursorId) {

		List<Journey> content = jpaQueryFactory
			.selectFrom(journey)
			.innerJoin(journey.journeyThemes, journeyTheme)
			.innerJoin(journey.member, member)
			.fetchJoin()
			.where(eqMemberId(memberId),
				eqCursorId(cursorId)
			)
			.groupBy(journey)
			.limit(pageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = false;
		if (content.size() > pageable.getPageSize()) {
			content.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(content, pageable, hasNext);

//		JPAQuery<Long> countQuery = jpaQueryFactory
//			.select(journey.countDistinct())
//			.from(journey)
//			.innerJoin(journey.journeyThemes, journeyTheme)
//			.where(eqMemberId(memberId));
//
//		return PageableExecutionUtils.getPage(content, pageable,
//			countQuery::fetchOne);
	}

	private BooleanExpression eqTitle(String title) {

		if (Objects.equals(title, "")) {
			return null;
		}

		return title == null ? null : journey.title.contains(title);
	}

	private void conRegions(List<String> regions, BooleanBuilder builder) {
		if (regions != null) {

			for (String region : regions) {
				builder.or(journey.region.eq(findByRegion(region)));
			}

		}
	}

	private void conJourneyTheme(List<String> journeyThemes,
		BooleanBuilder builder) {
		if (journeyThemes != null) {

			for (String theme : journeyThemes) {
				builder.or(
					journeyTheme.journeyThemeName.eq(findByTheme(theme)));
			}

		}
	}

	private BooleanExpression eqMemberId(String memberId) {

		return memberId == null ? null : journey.member.id.eq(memberId);
	}

	private BooleanExpression eqCursorId(Long cursorId) {

		if (cursorId != null) {
			return journey.id.gt(cursorId);
		}

		return null;
	}

}
