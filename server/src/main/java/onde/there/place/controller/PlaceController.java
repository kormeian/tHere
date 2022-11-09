package onde.there.place.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import onde.there.dto.place.PlaceDto;
import onde.there.dto.place.PlaceDto.Response;
import onde.there.member.security.jwt.TokenMemberId;
import onde.there.place.service.PlaceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

	private final PlaceService placeService;

	@Operation(summary = "장소 생성", description = "장소 생성, Amazon S3에 파일 업로드, 업로드 된 이미지 url 장소에 저장")
	@ApiResponse(responseCode = "201", description = "생성된 장소 반환", content = @Content(schema = @Schema(implementation = PlaceDto.Response.class)))
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<PlaceDto.Response> createPlace(
		@Parameter(description = "Image (여러 파일 업로드 가능)", required = true)
		@RequestPart List<MultipartFile> multipartFile,
		@Parameter(description = "장소 생성 정보", required = true,
			content = @Content(schema = @Schema(implementation = PlaceDto.CreateRequest.class)))
		@RequestPart @Valid PlaceDto.CreateRequest request,
		@Parameter(hidden = true)
		@TokenMemberId String memberId) {
		return ResponseEntity.ok(placeService.createPlace(multipartFile, request, memberId));
	}

	@Operation(summary = "장소 조회", description = "placeId에 해당하는 장소 조회")
	@ApiResponse(responseCode = "200", description = "조회한 장소 반환", content = @Content(schema = @Schema(implementation = PlaceDto.Response.class)))
	@GetMapping()
	public ResponseEntity<PlaceDto.Response> getPlace(
		@Parameter(description = "장소 아이디", required = true)
		@RequestParam Long placeId) {
		return ResponseEntity.ok(placeService.getPlace(placeId));
	}

	@Operation(summary = "장소 조회(list)", description = "journeyId 여정에 포함된 모든 장소 조회")
	@ApiResponse(responseCode = "200", description = "조회한 장소 list 반환", content = @Content(schema = @Schema(implementation = PlaceDto.Response.class)))
	@GetMapping("/list")
	public ResponseEntity<List<Response>> list(
		@Parameter(description = "여정 아이디", required = true)
		@RequestParam Long journeyId,
		@Parameter(hidden = true)
		@TokenMemberId String memberId) {
		return ResponseEntity.ok(placeService.list(journeyId, memberId));
	}

	@Operation(summary = "장소 삭제", description = "placeId에 해당하는 장소 삭제")
	@DeleteMapping()
	public ResponseEntity<?> deletePlace(
		@Parameter(description = "장소 아이디", required = true)
		@RequestParam Long placeId,
		@Parameter(hidden = true)
		@TokenMemberId String memberId) {
		return ResponseEntity.ok(placeService.delete(placeId, memberId));
	}

	@Operation(summary = "여정의 모든 장소 삭제", description = "journeyId에 해당하는 여정에 포함된 모든 장소 삭제")
	@DeleteMapping("/delete-all")
	public ResponseEntity<?> deleteAll(
		@Parameter(description = "여정 아이디", required = true)
		@RequestParam Long journeyId,
		@Parameter(hidden = true)
		@TokenMemberId String memberId) {
		return ResponseEntity.ok(placeService.deleteAll(journeyId, memberId));
	}

	@Operation(summary = "장소 업데이트", description = "입력한 값으로 장소 내용 수정")
	@ApiResponse(responseCode = "200", description = "업데이트된 장소 반환", content = @Content(schema = @Schema(implementation = PlaceDto.Response.class)))
	@PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<PlaceDto.Response> updatePlace(
		@Parameter(description = "Image (여러 파일 업로드 가능)", required = true)
		@RequestPart List<MultipartFile> multipartFile,
		@Parameter(description = "장소 업데이트 정보", required = true,
			content = @Content(schema = @Schema(implementation = PlaceDto.UpdateRequest.class)))
		@RequestPart @Valid PlaceDto.UpdateRequest request,
		@Parameter(hidden = true)
		@TokenMemberId String memberId) {
		return ResponseEntity.ok(placeService.updatePlace(multipartFile, request, memberId));
	}
}
