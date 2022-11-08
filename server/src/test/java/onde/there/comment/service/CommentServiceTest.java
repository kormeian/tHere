package onde.there.comment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import onde.there.comment.exception.CommentErrorCode;
import onde.there.comment.exception.CommentException;
import onde.there.comment.repository.CommentRepository;
import onde.there.domain.Comment;
import onde.there.domain.Member;
import onde.there.domain.Place;
import onde.there.dto.comment.CommentDto;
import onde.there.dto.comment.CommentDto.Response;
import onde.there.member.repository.MemberRepository;
import onde.there.place.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CommentServiceTest {

	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PlaceRepository placeRepository;
	@Autowired
	private CommentService commentService;

	@Test
	void 댓글_생성() {
		//given
		Member member = memberRepository.save(new Member("asd", "", "", ""));
		Place place = placeRepository.save(new Place());
		CommentDto.CreateRequest request =
			CommentDto.CreateRequest.builder()
				.placeId(place.getId())
				.text("댓글입니다.")
				.build();
		//when
		Comment comment = commentService.createComment(request, member.getId());
		//then
		assertEquals(member.getId(), comment.getMember().getId());
		assertEquals(place.getId(), comment.getPlace().getId());
		assertEquals(request.getText(), comment.getText());
	}

	@Test
	void 댓글_생성_에러_멤버아이디() {
		//given
		Member member = memberRepository.save(new Member("1", "", "", ""));
		Place place = placeRepository.save(new Place());
		CommentDto.CreateRequest request =
			CommentDto.CreateRequest.builder()
				.placeId(place.getId())
				.text("댓글입니다.")
				.build();
		//when
		CommentException commentException = assertThrows(CommentException.class,
			() -> commentService.createComment(request, "any member ID"));
		//then
		assertEquals(CommentErrorCode.NOT_FOUND_MEMBER, commentException.getErrorCode());
	}

	@Test
	void 댓글_생성_에러_장소아이디() {
		//given
		Member member = memberRepository.save(new Member("1", "", "", ""));
		Place place = placeRepository.save(new Place());
		CommentDto.CreateRequest request =
			CommentDto.CreateRequest.builder()
				.placeId(1000000000L)
				.text("댓글입니다.")
				.build();
		//when
		CommentException commentException = assertThrows(CommentException.class,
			() -> commentService.createComment(request, member.getId()));
		//then
		assertEquals(CommentErrorCode.NOT_FOUND_PLACE, commentException.getErrorCode());
	}

	@Test
	void 댓글_조회() {
		//given
		Member member = memberRepository.save(new Member("asd", "", "", ""));
		Place place = placeRepository.save(new Place());
		List<Comment> comments = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			String commentText = String.format("%d번째 댓글", i);
			comments.add(commentService.createComment(CommentDto.CreateRequest.builder()
					.placeId(place.getId())
					.text(commentText).build()
				, member.getId()));
		}
		Pageable pageable = PageRequest.of(0, 3);
		//when
		Page<Response> responses = commentService.getComments(place.getId(), pageable);
		//then
		for (Response response : responses) {
			System.out.println(response.getText());
			assertFalse(comments.contains(response.getText()));
		}
	}

	@Test
	void 댓글_조회_에러_장소아이디() {
		//given
		Member member = memberRepository.save(new Member("asd", "", "", ""));
		Place place = placeRepository.save(new Place());
		List<Comment> comments = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			String commentText = String.format("%d번째 댓글", i);
			comments.add(commentService.createComment(CommentDto.CreateRequest.builder()
					.placeId(place.getId())
					.text(commentText).build()
				, member.getId()));
		}
		Pageable pageable = PageRequest.of(0, 3);
		//when
		CommentException commentException = assertThrows(CommentException.class,
			() -> commentService.getComments(10000000L, pageable));
		//then
		assertEquals(CommentErrorCode.NOT_FOUND_PLACE, commentException.getErrorCode());
	}

	@Test
	void 댓글_조회_댓글없음() {
		//given
		Member member = memberRepository.save(new Member("asd", "", "", ""));
		Place place = placeRepository.save(new Place());

		//when

		Page<Response> commentList = commentService.getComments(place.getId(),
			PageRequest.of(0, 10));
		//then
		assertEquals(0, commentList.getContent().size());
	}

	@Test
	void 댓글_수정() {
		//given
		Member member = memberRepository.save(new Member("asd", "", "", ""));
		Place place = placeRepository.save(new Place());
		CommentDto.CreateRequest request =
			CommentDto.CreateRequest.builder()
				.placeId(place.getId())
				.text("댓글입니다.")
				.build();
		Comment comment = commentService.createComment(request, member.getId());

		CommentDto.UpdateRequest updateRequest =
			CommentDto.UpdateRequest.builder()
				.commentId(comment.getId())
				.text("수정된 댓글입니다.")
				.build();
		//when
		Comment updateComment = commentService.updateComment(updateRequest, member.getId());
		//then
		assertEquals(comment.getId(), updateComment.getId());
		assertEquals(updateRequest.getText(),
			commentRepository.findById(updateComment.getId()).orElseThrow().getText());
	}

	@Test
	void 댓글_수정_에러_댓글아이디() {
		//given
		Member member = memberRepository.save(new Member("asd", "", "", ""));
		Place place = placeRepository.save(new Place());
		CommentDto.CreateRequest request =
			CommentDto.CreateRequest.builder()
				.placeId(place.getId())
				.text("댓글입니다.")
				.build();
		Comment comment = commentService.createComment(request, member.getId());

		CommentDto.UpdateRequest updateRequest =
			CommentDto.UpdateRequest.builder()
				.commentId(place.getId())
				.text("수정된 댓글입니다.")
				.build();
		//when
		CommentException commentException = assertThrows(CommentException.class,
			() -> commentService.updateComment(updateRequest, member.getId()));
		//then
		assertEquals(CommentErrorCode.NOT_FOUND_COMMENT, commentException.getErrorCode());
	}

	@Test
	void 댓글_삭제() {
		//given
		Member member = memberRepository.save(new Member("asd", "", "", ""));
		Place place = placeRepository.save(new Place());
		CommentDto.CreateRequest request =
			CommentDto.CreateRequest.builder()
				.placeId(place.getId())
				.text("댓글입니다.")
				.build();
		Comment comment = commentService.createComment(request, member.getId());
		//when
		commentService.deleteComment(comment.getId(), member.getId());
		//then
		assertFalse(commentRepository.existsById(comment.getId()));
	}

	@Test
	void 댓글_삭제_에러_댓글아이디() {
		//given
		Member member = memberRepository.save(new Member("asd", "", "", ""));
		Place place = placeRepository.save(new Place());
		CommentDto.CreateRequest request =
			CommentDto.CreateRequest.builder()
				.placeId(place.getId())
				.text("댓글입니다.")
				.build();
		Comment comment = commentService.createComment(request, member.getId());
		//when
		CommentException commentException = assertThrows(CommentException.class,
			() -> commentService.deleteComment(100000000L, member.getId()));
		//then
		assertEquals(CommentErrorCode.NOT_FOUND_COMMENT, commentException.getErrorCode());
	}
}