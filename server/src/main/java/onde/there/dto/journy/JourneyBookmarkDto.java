package onde.there.dto.journy;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import onde.there.domain.Journey;
import onde.there.domain.JourneyBookmark;
import onde.there.domain.Member;
import onde.there.domain.type.RegionType;

@Getter
@Setter
public class JourneyBookmarkDto {

	@Schema(name = "북마크(찜) 조회 결과")
	@Getter
	@AllArgsConstructor
	@Builder
	public static class JourneyBookmarkPageResponse {

		private Long journeyId;
		private String nickName;
		private String profileImageUrl;
		private String title;
		private LocalDate startDate;
		private LocalDate endDate;
		private int numberOfPeople;
		private String disclosure;
		private List<String> journeyThemes;
		private String introductionText;
		private String region;
		private String journeyThumbnailUrl;

		public static JourneyBookmarkPageResponse fromEntity(JourneyBookmark journeyBookmark) {
			Journey journey = journeyBookmark.getJourney();
			Member member = journeyBookmark.getMember();
			return JourneyBookmarkPageResponse.builder()
				.journeyId(journey.getId())
				.nickName(member.getNickName())
				.profileImageUrl(member.getProfileImageUrl())
				.title(journey.getTitle())
				.startDate(journey.getStartDate())
				.endDate(journey.getEndDate())
				.numberOfPeople(journey.getNumberOfPeople())
				.disclosure(journey.getDisclosure())
				.journeyThemes(journey.getJourneyThemes().stream()
					.map(jt -> jt.getJourneyThemeName().getThemeName()).collect(
						Collectors.toList()))
				.introductionText(journey.getIntroductionText())
				.region(journey.getRegion().getRegionName())
				.journeyThumbnailUrl(journey.getJourneyThumbnailUrl())
				.build();
		}
	}
}
