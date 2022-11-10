package onde.there.journey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import onde.there.dto.journy.JourneyBookmarkDto.JourneyBookmarkPageResponse;
import onde.there.journey.service.JourneyBookmarkService;
import onde.there.member.security.jwt.TokenMemberId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class JourneyBookmarkController {

	private final JourneyBookmarkService journeyBookmarkService;

	@PostMapping
	@Operation(summary = "북마크(찜) 추가", description = "북마크(찜) 추가")
	public ResponseEntity<Void> createBookmark(
		@Parameter(description = "북마크 추가할 여정 id", required = true)
		@RequestParam Long journeyId,
		@Parameter(description = "헤더의 멤버 토큰", hidden = true)
		@TokenMemberId String memberId) {
		journeyBookmarkService.createBookmark(journeyId, memberId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	@Operation(summary = "북마크(찜) 삭제", description = "북마크(찜) 삭제")
	public ResponseEntity<Void> deleteBookmark(
		@Parameter(description = "북마크의 id", required = true)
		@RequestParam @NotNull Long journeyId,
		@Parameter(description = "헤더의 멤버 토큰", hidden = true)
		@TokenMemberId String memberId) {
		journeyBookmarkService.deleteBookmark(journeyId, memberId);
		return ResponseEntity.ok().build();
	}

	@GetMapping
	@Operation(summary = "해당 멤버의 북마크(찜) 가져오기", description = "북마크(찜) 가져오기")
	public ResponseEntity<Page<JourneyBookmarkPageResponse>> getBookmarkList(
		@Parameter(description = "헤더의 멤버 토큰", hidden = true)
		@TokenMemberId String memberId,
		Pageable pageable) {
		return ResponseEntity.ok(journeyBookmarkService.getBookmarkList(memberId, pageable));
	}
}
