package onde.there.comment.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class CommentException extends RuntimeException {
	private CommentErrorCode errorCode;
	private String errorMessage;

	public CommentException(CommentErrorCode errorCode){
		this.errorCode = errorCode;
		this.errorMessage = errorCode.getDescription();
	}
}