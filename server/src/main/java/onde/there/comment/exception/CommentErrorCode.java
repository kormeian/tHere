package onde.there.comment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentErrorCode {
	NOT_FOUND_PLACE("존재하지 않는 장소입니다."),
	NOT_FOUND_MEMBER("존재하지 않는 회원입니다."),
	NOT_FOUND_COMMENT("존재하지 않는 댓글입니다."),
	NOT_MATCH_MEMBER("본인 댓글이 아닙니다.")
	;
	private final String description;
}
