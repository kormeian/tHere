package onde.there.dto.journy;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onde.there.domain.Journey;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Getter
@Setter
@Schema
public class JourneyDto {

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Schema(name = "여정 생성 요청 parameter")
	public static class CreateRequest {

		@NotBlank(message = "제목을 입력해주세요.")
		@Schema(description = "여정 제목")
		private String title;

		@DateTimeFormat(iso = ISO.DATE)
		@Schema(description = "여정 시작 날짜")
		private LocalDate startDate;

		@DateTimeFormat(iso = ISO.DATE)
		@Schema(description = "여정 종료 날짜")
		private LocalDate endDate;

		@NotNull
		@Min(1)
		@Schema(description = "인원")
		private int numberOfPeople;

		@NotBlank(message = "공개범위를 선택해주세요.")
		@Schema(description = "공개 범위")
		private String disclosure;

		@NotNull
		@Schema(description = "여정 테마")
		private List<String> journeyThemes;

		@NotNull
		@Schema(description = "소개 글")
		private String introductionText;

		@NotNull
		@Schema(description = "지역")
		private String region;


	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class CreateResponse {

		private Long journeyId;
		private String nickName;
		private String title;
		private LocalDate startDate;
		private LocalDate endDate;
		private int numberOfPeople;
		private String disclosure;
		private String introductionText;
		private String journeyThumbnailUrl;
		private List<String> journeyThemes;
		private String region;
		private String profileImageUrl;


		public static JourneyDto.CreateResponse fromEntity(Journey journey,
			List<String> journeyThemes) {
			return CreateResponse.builder()
				.journeyId(journey.getId())
				.nickName(journey.getMember().getNickName())
				.title(journey.getTitle())
				.startDate(journey.getStartDate())
				.endDate(journey.getEndDate())
				.numberOfPeople(journey.getNumberOfPeople())
				.disclosure(journey.getDisclosure())
				.journeyThemes(journeyThemes)
				.introductionText(journey.getIntroductionText())
				.region(journey.getRegion().getRegionName())
				.journeyThumbnailUrl(journey.getJourneyThumbnailUrl())
				.profileImageUrl(journey.getMember().getProfileImageUrl())
				.build();
		}
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class JourneyListResponse {

		private Long journeyId;
		private String nickName;
		private String title;
		private LocalDate startDate;
		private LocalDate endDate;
		private int numberOfPeople;
		private String disclosure;
		private List<String> journeyThemes;
		private String introductionText;
		private String region;
		private String journeyThumbnailUrl;
		private String profileImageUrl;

		public static JourneyDto.JourneyListResponse fromEntity(Journey journey,
			List<String> journeyThemes) {
			return JourneyListResponse.builder()
				.journeyId(journey.getId())
				.nickName(journey.getMember().getNickName())
				.title(journey.getTitle())
				.startDate(journey.getStartDate())
				.endDate(journey.getEndDate())
				.numberOfPeople(journey.getNumberOfPeople())
				.disclosure(journey.getDisclosure())
				.journeyThemes(journeyThemes)
				.introductionText(journey.getIntroductionText())
				.region(journey.getRegion().getRegionName())
				.journeyThumbnailUrl(journey.getJourneyThumbnailUrl())
				.profileImageUrl(journey.getMember().getProfileImageUrl())
				.build();
		}

	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Schema(name = "여정 수정 요청 parameter")
	public static class UpdateRequest {

		@NotNull
		@Schema(description = "여정 아이디")
		private Long journeyId;

		@NotBlank(message = "제목을 입력해주세요.")
		@Schema(description = "여정 제목")
		private String title;

		@DateTimeFormat(iso = ISO.DATE)
		@Schema(description = "여정 시작 날짜")
		private LocalDate startDate;

		@DateTimeFormat(iso = ISO.DATE)
		@Schema(description = "여정 종료 날짜")
		private LocalDate endDate;

		@NotNull
		@Min(1)
		@Schema(description = "인원")
		private int numberOfPeople;

		@NotBlank(message = "공개범위를 선택해주세요.")
		@Schema(description = "공개 범위")
		private String disclosure;

		@NotNull
		@Schema(description = "여정 테마")
		private List<String> journeyThemes;

		@NotNull
		@Schema(description = "소개 글")
		private String introductionText;

		@NotNull
		@Schema(description = "지역")
		private String region;


	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class UpdateResponse {

		private Long journeyId;
		private String nickName;
		private String title;
		private LocalDate startDate;
		private LocalDate endDate;
		private int numberOfPeople;
		private String disclosure;
		private String introductionText;
		private String journeyThumbnailUrl;
		private List<String> journeyThemes;
		private String region;
		private String profileImageUrl;


		public static JourneyDto.UpdateResponse fromEntity(Journey journey,
			List<String> journeyThemes) {
			return UpdateResponse.builder()
				.journeyId(journey.getId())
				.nickName(journey.getMember().getNickName())
				.title(journey.getTitle())
				.startDate(journey.getStartDate())
				.endDate(journey.getEndDate())
				.numberOfPeople(journey.getNumberOfPeople())
				.disclosure(journey.getDisclosure())
				.journeyThemes(journeyThemes)
				.introductionText(journey.getIntroductionText())
				.region(journey.getRegion().getRegionName())
				.journeyThumbnailUrl(journey.getJourneyThumbnailUrl())
				.profileImageUrl(journey.getMember().getProfileImageUrl())
				.build();
		}
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class DetailResponse {

		private Long journeyId;
		private String nickName;
		private String title;
		private LocalDate startDate;
		private LocalDate endDate;
		private int numberOfPeople;
		private String disclosure;
		private String introductionText;
		private String journeyThumbnailUrl;
		private List<String> journeyThemes;
		private String region;
		private String profileImageUrl;


		public static JourneyDto.DetailResponse fromEntity(Journey journey,
			List<String> journeyThemes) {
			return DetailResponse.builder()
				.journeyId(journey.getId())
				.nickName(journey.getMember().getNickName())
				.title(journey.getTitle())
				.startDate(journey.getStartDate())
				.endDate(journey.getEndDate())
				.numberOfPeople(journey.getNumberOfPeople())
				.disclosure(journey.getDisclosure())
				.journeyThemes(journeyThemes)
				.introductionText(journey.getIntroductionText())
				.region(journey.getRegion().getRegionName())
				.journeyThumbnailUrl(journey.getJourneyThumbnailUrl())
				.profileImageUrl(journey.getMember().getProfileImageUrl())
				.build();
		}
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Schema(name = "여정 필터링 요청 parameter")
	public static class FilteringRequest {

		@Schema(description = "여정 제목")
		private String keyword;

		@Schema(description = "여정 테마")
		private List<String> themes;

		@Schema(description = "지역")
		private List<String> regions;


	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class FilteringResponse {

		private Long journeyId;
		private String nickName;
		private String title;
		private LocalDate startDate;
		private LocalDate endDate;
		private int numberOfPeople;
		private String disclosure;
		private String introductionText;
		private String journeyThumbnailUrl;
		private List<String> journeyThemes;
		private String region;
		private String profileImageUrl;


		public static JourneyDto.FilteringResponse fromEntity(Journey journey) {
			return FilteringResponse.builder()
				.journeyId(journey.getId())
				.nickName(journey.getMember().getNickName())
				.title(journey.getTitle())
				.startDate(journey.getStartDate())
				.endDate(journey.getEndDate())
				.numberOfPeople(journey.getNumberOfPeople())
				.disclosure(journey.getDisclosure())
				.journeyThemes(journey.getJourneyThemes().stream()
					.map(i -> i.getJourneyThemeName().getThemeName()).collect(
						Collectors.toList()))
				.introductionText(journey.getIntroductionText())
				.region(journey.getRegion().getRegionName())
				.journeyThumbnailUrl(journey.getJourneyThumbnailUrl())
				.profileImageUrl(journey.getMember().getProfileImageUrl())
				.build();
		}
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class MyListResponse {

		private Long journeyId;
		private String nickName;
		private String title;
		private LocalDate startDate;
		private LocalDate endDate;
		private int numberOfPeople;
		private String disclosure;
		private String introductionText;
		private String journeyThumbnailUrl;
		private List<String> journeyThemes;
		private String region;
		private String profileImageUrl;


		public static JourneyDto.MyListResponse fromEntity(Journey journey) {
			return MyListResponse.builder()
				.journeyId(journey.getId())
				.nickName(journey.getMember().getNickName())
				.title(journey.getTitle())
				.startDate(journey.getStartDate())
				.endDate(journey.getEndDate())
				.numberOfPeople(journey.getNumberOfPeople())
				.disclosure(journey.getDisclosure())
				.journeyThemes(journey.getJourneyThemes().stream()
					.map(i -> i.getJourneyThemeName().getThemeName()).collect(
						Collectors.toList()))
				.introductionText(journey.getIntroductionText())
				.region(journey.getRegion().getRegionName())
				.journeyThumbnailUrl(journey.getJourneyThumbnailUrl())
				.profileImageUrl(journey.getMember().getProfileImageUrl())
				.build();
		}
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class NickNameListResponse {

		private Long journeyId;
		private String nickName;
		private String title;
		private LocalDate startDate;
		private LocalDate endDate;
		private int numberOfPeople;
		private String disclosure;
		private String introductionText;
		private String journeyThumbnailUrl;
		private List<String> journeyThemes;
		private String region;
		private String profileImageUrl;


		public static JourneyDto.NickNameListResponse fromEntity(Journey journey) {
			return NickNameListResponse.builder()
				.journeyId(journey.getId())
				.nickName(journey.getMember().getNickName())
				.title(journey.getTitle())
				.startDate(journey.getStartDate())
				.endDate(journey.getEndDate())
				.numberOfPeople(journey.getNumberOfPeople())
				.disclosure(journey.getDisclosure())
				.journeyThemes(journey.getJourneyThemes().stream()
					.map(i -> i.getJourneyThemeName().getThemeName()).collect(
						Collectors.toList()))
				.introductionText(journey.getIntroductionText())
				.region(journey.getRegion().getRegionName())
				.journeyThumbnailUrl(journey.getJourneyThumbnailUrl())
				.profileImageUrl(journey.getMember().getProfileImageUrl())
				.build();
		}
	}

}
