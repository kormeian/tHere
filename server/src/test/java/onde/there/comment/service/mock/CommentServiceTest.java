package onde.there.comment.service.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import onde.there.comment.exception.CommentErrorCode;
import onde.there.comment.exception.CommentException;
import onde.there.comment.repository.CommentRepository;
import onde.there.comment.repository.CommentRepositoryImpl;
import onde.there.comment.service.CommentService;
import onde.there.domain.Comment;
import onde.there.domain.Member;
import onde.there.domain.Place;
import onde.there.dto.comment.CommentDto.CreateRequest;
import onde.there.dto.comment.CommentDto.Response;
import onde.there.dto.comment.CommentDto.UpdateRequest;
import onde.there.member.repository.MemberRepository;
import onde.there.place.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@Mock
	private CommentRepository commentRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private PlaceRepository placeRepository;
	@Mock
	private CommentRepositoryImpl commentRepositoryImpl;
	@InjectMocks
	private CommentService commentService;

	@DisplayName("01_00. create comment success")
	@Test
	public void test_01_00() {
		//given
		Member member = getMember();
		Place place = getPlace();
		Comment comment = getComment(member, place);
		given(memberRepository.findById(any())).willReturn(Optional.of(member));
		given(placeRepository.findById(any())).willReturn(Optional.of(place));
		given(commentRepository.save(any())).willReturn(comment);

		ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
		//when
		CreateRequest request = getCreateRequest();

		commentService.createComment(request, "memberId");

		//then
		verify(commentRepository, times(1)).save(commentCaptor.capture());

		assertEquals(commentCaptor.getValue().getText(), "test text");
		assertEquals(commentCaptor.getValue().getPlace().getId(), 1L);
	}

	@DisplayName("01_01. create comment fail not found member")
	@Test
	public void test_01_01() {
		Member member = getMember();
		given(memberRepository.findById(any())).willReturn(Optional.of(member));
		given(placeRepository.findById(any())).willReturn(Optional.empty());

		//when
		CreateRequest request = getCreateRequest();

		CommentException commentException = assertThrows(CommentException.class,
			() -> commentService.createComment(request, "memberId"));

		//then
		assertEquals(commentException.getErrorCode(), CommentErrorCode.NOT_FOUND_PLACE);
	}


	@DisplayName("01_02. create comment fail not found member")
	@Test
	public void test_01_02() {
		//given
		given(memberRepository.findById(any())).willReturn(Optional.empty());

		//when
		CreateRequest request = getCreateRequest();

		CommentException commentException = assertThrows(CommentException.class,
			() -> commentService.createComment(request, "memberId"));

		//then
		assertEquals(commentException.getErrorCode(), CommentErrorCode.NOT_FOUND_MEMBER);
	}

	@DisplayName("02_00. get comments success")
	@Test
	public void test_02_00() {
		//given
		Member member = getMember();
		Place place = getPlace();
		Comment comment = getComment(member, place);
		Response response = getResponse(comment);

		Page<Response> page = new PageImpl<>(List.of(response));
		given(placeRepository.existsById(any())).willReturn(true);
		given(commentRepositoryImpl.getCommentPage(any(), any())).willReturn(page);

		//when
		Page<Response> comments = commentService.getComments(1L, PageRequest.of(0, 5));

		//then
		assertEquals(comments.getSize(), 1);
	}

	@DisplayName("02_01. get comments fail not found place")
	@Test
	public void test_02_01() {
		//given
		given(placeRepository.existsById(any())).willReturn(false);

		//when
		CommentException commentException = assertThrows(CommentException.class,
			() -> commentService.getComments(1L, PageRequest.of(0, 5)));

		//then
		assertEquals(commentException.getErrorCode(), CommentErrorCode.NOT_FOUND_PLACE);
	}

	@DisplayName("03_00. update comment success")
	@Test
	public void test_03_00() {
		//given
		Member member = getMember();
		Place place = getPlace();
		Comment comment = getComment(member, place);

		given(commentRepository.findById(any())).willReturn(Optional.of(comment));
		ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);

		//when
		UpdateRequest request = UpdateRequest.builder()
			.commentId(1L)
			.text("update text")
			.build();

		commentService.updateComment(request, "memberId");

		//then
		verify(commentRepository, times(1)).save(commentCaptor.capture());

		assertEquals(commentCaptor.getValue().getText(), "update text");
	}

	@DisplayName("03_01. update comment fail not found comment")
	@Test
	public void test_03_01() {
		//given
		given(commentRepository.findById(any())).willReturn(Optional.empty());

		//when
		UpdateRequest request = UpdateRequest.builder()
			.commentId(1L)
			.text("update text")
			.build();

		CommentException commentException = assertThrows(CommentException.class,
			() -> commentService.updateComment(request, "memberId"));

		//then
		assertEquals(commentException.getErrorCode(), CommentErrorCode.NOT_FOUND_COMMENT);
	}

	@DisplayName("03_02. update comment fail not match member")
	@Test
	public void test_03_02() {
		//given
		Member member = getMember();
		Place place = getPlace();
		Comment comment = getComment(member, place);

		given(commentRepository.findById(any())).willReturn(Optional.of(comment));

		//when
		UpdateRequest request = UpdateRequest.builder()
			.commentId(1L)
			.text("update text")
			.build();

		CommentException commentException = assertThrows(CommentException.class,
			() -> commentService.updateComment(request, "mismatch"));

		//then
		assertEquals(commentException.getErrorCode(), CommentErrorCode.NOT_MATCH_MEMBER);
	}

	@DisplayName("04_00. delete success")
	@Test
	public void test_04_00() {
		//given
		Member member = getMember();
		Place place = getPlace();
		Comment comment = getComment(member, place);

		given(commentRepository.findById(any())).willReturn(Optional.of(comment));

		//when
		commentService.deleteComment(1L, "memberId");

		//then
		verify(commentRepository, times(1)).delete(any());
	}

	@DisplayName("04_01. delete fail not found comment")
	@Test
	public void test_04_01() {
		//given
		given(commentRepository.findById(any())).willReturn(Optional.empty());

		//when
		CommentException commentException = assertThrows(CommentException.class,
			() -> commentService.deleteComment(1L, "memberId"));

		//then
		assertEquals(commentException.getErrorCode(), CommentErrorCode.NOT_FOUND_COMMENT);
	}

	@DisplayName("04_02. delete fail not match member")
	@Test
	public void test_04_02(){
		//given
		Member member = getMember();
		Place place = getPlace();
		Comment comment = getComment(member, place);

		given(commentRepository.findById(any())).willReturn(Optional.of(comment));

		//when
		CommentException commentException = assertThrows(CommentException.class,
			() -> commentService.deleteComment(1L, "mismatch"));

		//then
		assertEquals(commentException.getErrorCode(), CommentErrorCode.NOT_MATCH_MEMBER);
	}

	private static CreateRequest getCreateRequest() {
		return CreateRequest.builder()
			.placeId(1L)
			.text("test text")
			.build();
	}

	private static Comment getComment(Member member, Place place) {
		return Comment.builder().id(1L).place(place).member(member).build();
	}

	private static Place getPlace() {
		return Place.builder().id(1L).build();
	}

	private static Member getMember() {
		return Member.builder().id("memberId").nickName("nickName test").build();
	}

	private Response getResponse(Comment comment) {
		return Response.builder()
			.commentId(comment.getId())
			.memberId(comment.getMember().getId())
			.memberNickName(comment.getMember().getNickName())
			.memberProfileImageUrl(comment.getMember().getProfileImageUrl())
			.placeId(comment.getPlace().getId())
			.text(comment.getText())
			.build();
	}
}