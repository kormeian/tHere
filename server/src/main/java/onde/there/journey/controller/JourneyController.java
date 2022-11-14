package onde.there.journey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import onde.there.dto.journy.JourneyDto;
import onde.there.dto.journy.JourneyDto.DetailResponse;
import onde.there.dto.journy.JourneyDto.FilteringRequest;
import onde.there.dto.journy.JourneyDto.FilteringResponse;
import onde.there.dto.journy.JourneyDto.JourneyListResponse;
import onde.there.dto.journy.JourneyDto.MyListResponse;
import onde.there.dto.journy.JourneyDto.NickNameListResponse;
import onde.there.journey.service.JourneyService;
import onde.there.member.security.jwt.TokenMemberId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/journey")
public class JourneyController {

	private final JourneyService journeyService;

	@Operation(summary = "여정 생성",
		description = "여정을 생성하고, 생성된 여정에 입력받은 여정 테마를 생성")
	@ApiResponse(responseCode = "200", description = "생성된 여정 반환",
		content = @Content(schema = @Schema(implementation = JourneyDto.CreateResponse.class)))
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
		MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<JourneyDto.CreateResponse> createJourney(
		@Parameter(name = "여정 정보", description = "생성할 여정에 대한 정보", required = true,
			content = @Content(schema = @Schema(implementation = JourneyDto.CreateRequest.class)))
		@RequestPart @Valid JourneyDto.CreateRequest request,
		@Parameter(description = "Thumbnail 이미지 파일", required = true)
		@RequestPart MultipartFile thumbnail,
		@TokenMemberId String memberId) {

		return ResponseEntity.ok(
			journeyService.createJourney(request, thumbnail, memberId));
	}

	@Operation(summary = "여정 수정", description = "여정을 수정합니다.")
	@ApiResponse(responseCode = "200", description = "수정된 여정 반환",
		content = @Content(schema = @Schema(implementation = JourneyDto.UpdateResponse.class)))
	@PatchMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
		MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<JourneyDto.UpdateResponse> updateJourney(
		@Parameter(name = "여정 정보", description = "수정할 여정에 대한 정보", required = true,
			content = @Content(schema = @Schema(implementation = JourneyDto.UpdateRequest.class)))
		@RequestPart @Valid JourneyDto.UpdateRequest request,
		@Parameter(description = "Thumbnail 이미지 파일", required = false)
		@RequestPart(required = false) MultipartFile thumbnail,
		@TokenMemberId String memberId) {

		return ResponseEntity.ok(
			journeyService.updateJourney(request, thumbnail, memberId));
	}

	@Operation(summary = "여정 삭제", description = "여정을 삭제합니다.")
	@ApiResponse(responseCode = "200", description = "삭제된 여정 id 반환")
	@DeleteMapping
	public ResponseEntity<String> deleteJourney(
		@Parameter(description = "삭제할 여정 id", required = true)
		@RequestParam Long journeyId,
		@TokenMemberId String memberId) {

		journeyService.deleteJourney(journeyId, memberId);

		return ResponseEntity.ok("journeyId : " + journeyId);
	}

	@Operation(summary = "여정 조회", description = "여정을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "조회한 여정을 반환",
		content = @Content(schema = @Schema(implementation = JourneyDto.DetailResponse.class)))
	@GetMapping("/detail")
	public ResponseEntity<DetailResponse> getJourneyDetail(
		@Parameter(description = "조회할 여정 id", required = true)
		@RequestParam Long journeyId,
		@TokenMemberId String memeberId
	) {

		return ResponseEntity.ok(journeyService.journeyDetail(journeyId, memeberId));
	}

	@Operation(summary = "모든 여정 조회", description = "모든 여정을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "모든 여정을 반환",
		content = @Content(schema = @Schema(implementation = JourneyDto.JourneyListResponse.class)))
	@GetMapping("/list")
	public ResponseEntity<List<JourneyListResponse>> getJourneyList() {

		return ResponseEntity.ok(journeyService.list());
	}

	@Operation(summary = "내 여정 조회", description = "내 여정을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "내 여정을 반환",
		content = @Content(schema = @Schema(implementation = JourneyDto.MyListResponse.class)))
	@GetMapping("/my-list")
	public ResponseEntity<Page<MyListResponse>> getMyJourneyList(
		@Parameter(description = "내 아이디", required = true)
		@TokenMemberId String memberId, Pageable pageable) {

		return ResponseEntity.ok(journeyService.myList(memberId, pageable));
	}

	@Operation(summary = "nickName 여정 조회", description = "nickName 여정을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "nickName 여정을 반환",
		content = @Content(schema = @Schema(implementation = JourneyDto.NickNameListResponse.class)))
	@GetMapping("/nickName-list")
	public ResponseEntity<Page<NickNameListResponse>> getNickNameJourneyList(
		@Parameter(description = "닉네임", required = true)
		String nickName, Pageable pageable, @TokenMemberId String memberId) {

		return ResponseEntity.ok(journeyService.nickNameList(nickName, pageable, memberId));
	}

	@Operation(summary = "여정 필터링", description = "필터링된 여정을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "필터링된 여정을 반환",
		content = @Content(schema = @Schema(implementation = JourneyDto.FilteringResponse.class)))
	@GetMapping("/filtered-list")
	public ResponseEntity<Page<FilteringResponse>> getFilteredList(
		@RequestParam String keyword,
		@RequestParam List<String> themes,
		@RequestParam List<String> regions,
		Pageable pageable,
		@TokenMemberId String memberId
	) {

		FilteringRequest filteringRequest = FilteringRequest.builder()
			.keyword(keyword)
			.themes(themes)
			.regions(regions)
			.build();

		return ResponseEntity.ok(
			journeyService.filteredList(filteringRequest, pageable, memberId));
	}

}
