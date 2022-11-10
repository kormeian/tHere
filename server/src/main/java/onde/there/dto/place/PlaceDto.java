package onde.there.dto.place;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import onde.there.domain.Journey;
import onde.there.domain.Place;
import onde.there.domain.PlaceImage;
import onde.there.domain.type.PlaceCategoryType;

@Getter
@Setter
@Schema
public class PlaceDto {


	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	@Builder
	@Schema(name = "장소 생성에 필요한 request파라미터")
	public static class CreateRequest {

		@NotNull(message = "latitude 값을 입력 해 주세요!")
		private Double latitude;
		@NotNull(message = "longitude 값을 입력 해 주세요!")
		private Double longitude;
		@NotBlank(message = "title 값을 입력 해 주세요!")
		@Size(max = 200, message = "200 글자 이하로 작성해 주세요")
		private String title;
		@Size(max = 1000, message = "1000 글자 이하로 작성해 주세요")
		private String text;
		@NotBlank(message = "addressName 값을 입력 해 주세요!")
		private String addressName;
		private String region1;
		private String region2;
		private String region3;
		private String region4;
		@NotNull(message = "placeTime 값을 입력 해 주세요!")
		@Past(message = "현재 시간보다 과거의 시간을 입력해주세요!")
		private LocalDateTime placeTime;
		@NotNull(message = "journeyId 값을 입력 해 주세요!")
		private Long journeyId;

		@NotBlank(message = "placeCategory 값을 입력 해 주세요!")
		private String placeCategory;
		@NotBlank(message = "placeName 값을 입력 해 주세요!")
		private String placeName;

		public Place toEntity() {
			return Place.builder()
				.latitude(this.latitude)
				.longitude(this.longitude)
				.title(this.title)
				.text(this.text)
				.addressName(this.addressName)
				.region1(this.region1)
				.region2(this.region2)
				.region3(this.region3)
				.region4(this.region4)
				.placeTime(this.placeTime)
				.placeCategory(PlaceCategoryType.toPlaceCategoryType(this.placeCategory))
				.placeName(this.placeName)
				.build();
		}
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	@Builder
	@Schema(name = "장소 업데이트에 필요한 request 파라미터")
	public static class UpdateRequest {

		@NotNull(message = "placeId 값을 입력 해 주세요!")
		private Long placeId;
		@NotNull(message = "latitude 값을 입력 해 주세요!")
		private Double latitude;
		@NotNull(message = "longitude 값을 입력 해 주세요!")
		private Double longitude;
		@NotBlank(message = "title 값을 입력 해 주세요!")
		@Size(max = 200, message = "200 글자 이하로 작성해 주세요")
		private String title;
		@Size(max = 1000, message = "1000 글자 이하로 작성해 주세요")
		private String text;
		@NotBlank(message = "addressName 값을 입력 해 주세요!")
		private String addressName;
		private String region1;
		private String region2;
		private String region3;
		private String region4;
		@NotNull(message = "placeTime 값을 입력 해 주세요!")
		@Past(message = "현재 시간보다 과거의 시간을 입력해주세요!")
		private LocalDateTime placeTime;
		@NotNull(message = "journeyId 값을 입력 해 주세요!")
		private Long journeyId;

		@NotBlank(message = "placeCategory 값을 입력 해 주세요!")
		private String placeCategory;
		@NotBlank(message = "placeName 값을 입력 해 주세요!")
		private String placeName;

		public Place toEntity(Journey journey) {
			return Place.builder()
				.id(this.getPlaceId())
				.latitude(this.latitude)
				.longitude(this.longitude)
				.title(this.title)
				.text(this.text)
				.addressName(this.addressName)
				.region1(this.region1)
				.region2(this.region2)
				.region3(this.region3)
				.region4(this.region4)
				.placeTime(this.placeTime)
				.placeCategory(PlaceCategoryType.toPlaceCategoryType(this.placeCategory))
				.placeName(this.placeName)
				.placeImages(new ArrayList<>())
				.journey(journey)
				.build();
		}
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	@Builder
	@Schema(name = "장소 조회시 반환되는 response")
	public static class Response {

		private Long placeId;
		private Double latitude;
		private Double longitude;

		private String title;
		private String text;

		private String addressName;
		private String region1;
		private String region2;
		private String region3;
		private String region4;
		private String placeName;

		private LocalDateTime placeTime;
		private String placeCategory;
		private String placeHeartCount;
		private Long journeyId;
		private boolean heartedCheck;

		private List<String> imageUrls = new ArrayList<>();

		public static Response toResponse(Place place) {
			return Response.builder()
				.placeId(place.getId())
				.latitude(place.getLatitude())
				.longitude(place.getLongitude())
				.title(place.getTitle())
				.text(place.getText())
				.addressName(place.getAddressName())
				.region1(place.getRegion1())
				.region2(place.getRegion2())
				.region3(place.getRegion3())
				.region4(place.getRegion4())
				.placeName(place.getPlaceName())
				.placeTime(place.getPlaceTime())
				.placeCategory(place.getPlaceCategory().getDescription())
				.placeName(place.getPlaceName())
				.placeHeartCount(place.getPlaceHeartCount() >= 1000 ?
					(place.getPlaceHeartCount() / 1000) + "k" :
					String.valueOf(place.getPlaceHeartCount()))
				.journeyId(place.getJourney().getId())
				.imageUrls(place.getPlaceImages()
					.stream().map(PlaceImage::getUrl).collect(Collectors.toList()))
				.build();
		}

		public static List<Response> toResponse(List<Place> list) {
			return list.stream().map(Response::toResponse).collect(Collectors.toList());
		}
	}
}
