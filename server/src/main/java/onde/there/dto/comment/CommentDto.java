package onde.there.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {

	@Schema(name = "댓글 생성 요청")
	@Getter
	@Builder
	public static class CreateRequest {

		@NotNull
		private Long placeId;
		@NotNull
		private String text;
	}

	@Schema(name = "댓글 수정 요청")
	@Getter
	@Builder
	public static class UpdateRequest {

		@NotNull
		private Long commentId;
		@NotNull
		private String text;
	}

	@Schema(name = "댓글 조회 결과")
	@Builder
	@Getter
	@AllArgsConstructor
	public static class Response {

		private Long commentId;
		private String memberId;
		private String memberNickName;
		private String memberProfileImageUrl;
		private Long placeId;
		private String text;

	}
}
