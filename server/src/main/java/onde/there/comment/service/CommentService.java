package onde.there.comment.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.comment.exception.CommentException;
import onde.there.comment.repository.CommentRepository;
import onde.there.comment.repository.CommentRepositoryImpl;
import onde.there.comment.exception.CommentErrorCode;
import onde.there.domain.Comment;
import onde.there.dto.comment.CommentDto;
import onde.there.dto.comment.CommentDto.CreateRequest;
import onde.there.dto.comment.CommentDto.Response;
import onde.there.member.repository.MemberRepository;
import onde.there.place.repository.PlaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommentRepositoryImpl commentRepositoryImpl;
	private final MemberRepository memberRepository;
	private final PlaceRepository placeRepository;

	@Transactional
	public Comment createComment(CreateRequest request, String memberId) {
		Comment comment = commentRepository.save(Comment.builder()
			.member(memberRepository.findById(memberId)
				.orElseThrow(() -> new CommentException(CommentErrorCode.NOT_FOUND_MEMBER)))
			.place(placeRepository.findById(request.getPlaceId())
				.orElseThrow(() -> new CommentException(CommentErrorCode.NOT_FOUND_PLACE)))
			.text(request.getText())
			.build());
		log.info(comment.getMember().getNickName() + "님이 \"" + comment.getPlace().getTitle()
			+ "\"에 댓글을 남겼습니다.");
		return comment;
	}

	@Transactional(readOnly = true)
	public Page<Response> getComments(Long placeId, Pageable pageable) {
		log.info("댓글 조회 시작");
		if (!placeRepository.existsById(placeId)) {
			throw new CommentException(CommentErrorCode.NOT_FOUND_PLACE);
		}
		return commentRepositoryImpl.getCommentPage(placeId, pageable);
	}

	@Transactional
	public Comment updateComment(CommentDto.UpdateRequest request, String memberId) {
		Comment comment = commentRepository.findById(request.getCommentId())
			.orElseThrow(() -> new CommentException(CommentErrorCode.NOT_FOUND_COMMENT));
		if (!Objects.equals(comment.getMember().getId(), memberId)) {
			throw new CommentException(CommentErrorCode.NOT_MATCH_MEMBER);
		}
		String preComment = comment.getText();
		comment.updateComment(request.getText());
		log.info("댓글 아이디 : " + comment.getId() + "의 댓글 \"" + preComment + "\"에서 \""
			+ comment.getText() + "\"로 업데이트 되었습니다.");
		return commentRepository.save(comment);
	}

	@Transactional
	public void deleteComment(Long commentId, String memberId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentErrorCode.NOT_FOUND_COMMENT));
		if (!Objects.equals(comment.getMember().getId(), memberId)) {
			throw new CommentException(CommentErrorCode.NOT_MATCH_MEMBER);
		}
		commentRepository.delete(comment);
		log.info("댓글 아이디 : " + comment.getId() + "의 댓글 \"" + comment.getText() + "\" 삭제 완료");
	}
}
