package onde.there.comment.repository;

import static onde.there.domain.QComment.comment;
import static onde.there.domain.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import onde.there.domain.Comment;
import onde.there.dto.comment.CommentDto;
import onde.there.dto.comment.CommentDto.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

	private final JPQLQueryFactory jpaQueryFactory;

	@Override
	public Page<CommentDto.Response> getCommentPage(Long placeId, Pageable pageable) {
		List<Response> fetch = jpaQueryFactory
			.select(Projections.constructor(Response.class,
				comment.id.as("commentId"),
				member.id.as("memberId"),
				member.nickName.as("memberNickName"),
				member.profileImageUrl.as("memberProfileImageUrl"),
				comment.place.id.as("placeId"),
				comment.text))
			.from(comment)
			.leftJoin(comment.member, member)
			.where(
				placeEq(placeId)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		JPQLQuery<Comment> count = jpaQueryFactory
			.selectFrom(comment)
			.leftJoin(comment.member, member)
			.where(placeEq(placeId));
		return PageableExecutionUtils.getPage(fetch, pageable, count::fetchCount);
	}

	private BooleanExpression placeEq(Long placeId) {
		return placeId != null ? comment.place.id.eq(placeId) : null;
	}
}
