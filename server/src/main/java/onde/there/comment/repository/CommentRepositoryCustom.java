package onde.there.comment.repository;

import onde.there.dto.comment.CommentDto;
import onde.there.dto.comment.CommentDto.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
	Page<Response> getCommentPage(Long placeId, Pageable pageable);
}
