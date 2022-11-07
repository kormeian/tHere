package onde.there.place.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import onde.there.member.security.jwt.TokenMemberId;
import onde.there.place.service.PlaceHeartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceHeartController {

	private final PlaceHeartService placeHeartService;

	@Operation(summary = "좋아요 업", description = "좋아요 누르기")
	@PostMapping("/heart")
	public ResponseEntity<?> heart(
		@Parameter(description = "장소 아이디", required = true)
		@RequestParam Long placeId,
		@Parameter(hidden = true) @TokenMemberId String memberId) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(placeHeartService.heart(placeId, memberId));
	}

	@Operation(summary = "좋아요 다운", description = "좋아요 취소")
	@PostMapping("/unheart")
	public ResponseEntity<?> unHeart(
		@Parameter(description = "장소 아이디", required = true)
		@RequestParam Long placeId,
		@Parameter(hidden = true)
		@TokenMemberId String memberId) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(placeHeartService.unHeart(placeId, memberId));
	}
}
