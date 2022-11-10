package onde.there.place.service.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import onde.there.comment.repository.CommentRepository;
import onde.there.domain.Comment;
import onde.there.domain.Journey;
import onde.there.domain.Member;
import onde.there.domain.Place;
import onde.there.domain.PlaceHeart;
import onde.there.domain.PlaceImage;
import onde.there.domain.type.PlaceCategoryType;
import onde.there.dto.place.PlaceDto;
import onde.there.dto.place.PlaceDto.CreateRequest;
import onde.there.dto.place.PlaceDto.Response;
import onde.there.dto.place.PlaceDto.UpdateRequest;
import onde.there.image.service.AwsS3Service;
import onde.there.journey.repository.JourneyRepository;
import onde.there.member.repository.MemberRepository;
import onde.there.place.exception.PlaceErrorCode;
import onde.there.place.exception.PlaceException;
import onde.there.place.repository.PlaceHeartRepository;
import onde.there.place.repository.PlaceImageRepository;
import onde.there.place.repository.PlaceRepository;
import onde.there.place.repository.PlaceRepositoryCustomImpl;
import onde.there.place.service.PlaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

	@InjectMocks
	private PlaceService placeService;

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private PlaceImageRepository placeImageRepository;

	@Mock
	private JourneyRepository journeyRepository;

	@Mock
	private PlaceRepositoryCustomImpl placeRepositoryCustom;

	@Mock
	private PlaceHeartRepository placeHeartRepository;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private AwsS3Service awsS3Service;

	@DisplayName("01_00. createPlace success")
	@Test
	public void test_01_00() throws IOException {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		Place place = getPlace(journey);
		PlaceImage placeImage = getPlaceImage(place, "url1");

		given(journeyRepository.findById(any())).willReturn(Optional.of(journey));
		given(placeRepository.countAllByJourneyId(any())).willReturn(9L);
		given(awsS3Service.uploadFiles(any())).willReturn(List.of("url1"));
		given(placeRepository.save(any())).willReturn(getPlace(journey));
		given(placeImageRepository.save(any())).willReturn(placeImage);

		//when
		ArgumentCaptor<Place> placeCaptor = ArgumentCaptor.forClass(Place.class);
		ArgumentCaptor<PlaceImage> placeImageCaptor = ArgumentCaptor.forClass(PlaceImage.class);

		List<MultipartFile> multipartFile = getMultipartFiles();
		PlaceDto.CreateRequest request = getCreateRequest(journey);
		Response response = placeService.createPlace(multipartFile, request, member.getId());

		//then
		verify(placeRepository, times(1)).save(placeCaptor.capture());
		verify(placeImageRepository, times(1)).save(placeImageCaptor.capture());

		Place value = placeCaptor.getValue();
		assertEquals(value.getText(), request.getText());
		assertEquals(value.getTitle(), request.getTitle());
	}

	@DisplayName("01_01. createPlace fail not found journey")
	@Test
	public void test_01_01() throws IOException {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);

		given(journeyRepository.findById(any())).willReturn(Optional.empty());

		//when
		List<MultipartFile> multipartFile = getMultipartFiles();
		PlaceDto.CreateRequest request = getCreateRequest(journey);
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.createPlace(multipartFile, request, member.getId()));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_JOURNEY);
	}

	@DisplayName("01_02. createPlace fail mismatch memberId")
	@Test
	public void test_01_02() throws IOException {
		//given
		Member member = getMember();
		Journey journey = getJourney(Member.builder().id("mismatch").build());

		given(journeyRepository.findById(any())).willReturn(Optional.of(journey));

		//when
		List<MultipartFile> multipartFile = getMultipartFiles();
		PlaceDto.CreateRequest request = getCreateRequest(journey);
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.createPlace(multipartFile, request, member.getId()));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.MISMATCH_MEMBER_ID);
	}

	@DisplayName("01_03. createPlace fail mismatch place category type")
	@Test
	public void test_01_03() throws IOException {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);

		given(journeyRepository.findById(any())).willReturn(Optional.of(journey));
		given(placeRepository.countAllByJourneyId(any())).willReturn(9L);

		//when
		List<MultipartFile> multipartFile = getMultipartFiles();
		PlaceDto.CreateRequest request = getCreateRequest(journey);
		request.setPlaceCategory("mismatch");
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.createPlace(multipartFile, request, member.getId()));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.MISMATCH_PLACE_CATEGORY_TYPE);
	}

	@DisplayName("02_00. getPlace success")
	@Test
	public void test_02_00() {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		Place place = getPlace(journey);
		PlaceImage placeImage = getPlaceImage(place, "url1");
		place.setPlaceImages(List.of(placeImage));

		given(placeRepository.findById(any())).willReturn(Optional.of(place));

		//when
		Response response = placeService.getPlace(1L);

		//then
		assertEquals(response.getPlaceId(), place.getId());
		assertEquals(response.getJourneyId(), place.getJourney().getId());
		assertEquals(response.getPlaceCategory(), place.getPlaceCategory().getDescription());
	}

	@DisplayName("02_01. getPlace fail not found place")
	@Test
	public void test_02_01() {
		//given
		given(placeRepository.findById(any())).willReturn(Optional.empty());

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.getPlace(1L));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_PLACE);
	}

	@DisplayName("03_00. placeListOfJourney success memberId not null")
	@Test
	public void test_03_00() {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		Place place = getPlace(journey);
		PlaceImage placeImage = getPlaceImage(place, "url0");
		PlaceImage placeImage1 = getPlaceImage(place, "url1");
		place.setPlaceImages(List.of(placeImage, placeImage1));

		given(journeyRepository.findById(any())).willReturn(Optional.of(journey));
		given(placeRepositoryCustom.findAllByJourneyOrderByPlaceTimeAsc(any(), anyString()))
			.willReturn(List.of(Response.toResponse(place), Response.toResponse(place)));

		//when
		List<Response> responses = placeService.placeListOfJourney(1L, "memberId");

		//then
		assertEquals(responses.size(), 2);
		assertEquals(responses.get(0).getImageUrls().get(0), "url0");
		assertEquals(responses.get(0).getJourneyId(), responses.get(1).getJourneyId());
	}

	@DisplayName("03_01. placeListOfJourney success memberId null")
	@Test
	public void test_03_01() {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		Place place = getPlace(journey);
		PlaceImage placeImage = getPlaceImage(place, "url0");
		PlaceImage placeImage1 = getPlaceImage(place, "url1");
		place.setPlaceImages(List.of(placeImage, placeImage1));

		given(journeyRepository.findById(any())).willReturn(Optional.of(journey));
		given(placeRepository.findAllByJourneyIdOrderByPlaceTimeAsc(journey.getId()))
			.willReturn(List.of(place, place));

		//when
		List<Response> responses = placeService.placeListOfJourney(1L, null);

		//then
		assertEquals(responses.size(), 2);
		assertEquals(responses.get(0).getImageUrls().get(0), "url0");
		assertEquals(responses.get(0).getJourneyId(), responses.get(1).getJourneyId());
	}

	@DisplayName("03_02. placeListOfJourney fail not found journey")
	@Test
	public void test_03_02() {
		//given
		given(journeyRepository.findById(any())).willReturn(Optional.empty());

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.placeListOfJourney(1L, null));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_JOURNEY);
	}

	@DisplayName("04_00. deletePlace success")
	@Test
	public void test_04_00() {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		Place place = getPlace(journey);
		PlaceImage placeImage = getPlaceImage(place, "url0");
		PlaceImage placeImage1 = getPlaceImage(place, "url1");
		List<PlaceImage> placeImages = new ArrayList<>(List.of(placeImage, placeImage1));
		place.setPlaceImages(placeImages);
		PlaceHeart heart = getHeart(member, place);
		Comment comment = getComment(member, place);

		given(placeRepository.findById(any())).willReturn(Optional.of(place));
		given(placeImageRepository.findAllByPlaceId(any())).willReturn(placeImages);
		given(commentRepository.findAllByPlaceId(any())).willReturn(List.of(comment));
		given(placeHeartRepository.findAllByPlaceId(any())).willReturn(List.of(heart));

		//when
		boolean deletePlace = placeService.deletePlace(1L, "memberId");

		//then
		verify(placeRepository, times(1)).delete(any());
		verify(placeImageRepository, times(2)).delete(any());
		verify(placeHeartRepository, times(1)).deleteAll(any());
		verify(commentRepository, times(1)).deleteAll(any());

		assertTrue(deletePlace);
	}

	@DisplayName("04_01. deletePlace fail not found place")
	@Test
	public void test_04_01() {
		//given
		given(placeRepository.findById(any())).willReturn(Optional.empty());

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.deletePlace(1L, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_PLACE);
	}

	@DisplayName("04_02. deletePlace fail mismatch memberId")
	@Test
	public void test_04_02() {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		Place place = getPlace(journey);

		given(placeRepository.findById(any())).willReturn(Optional.of(place));

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.deletePlace(1L, "mismatch"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.MISMATCH_MEMBER_ID);
	}

	@DisplayName("05_00. deleteAll success")
	@Test
	public void test_05_00() {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		Place place = getPlace(journey);
		PlaceImage placeImage = getPlaceImage(place, "url0");
		PlaceImage placeImage1 = getPlaceImage(place, "url1");
		List<PlaceImage> placeImages = new ArrayList<>(List.of(placeImage, placeImage1));
		place.setPlaceImages(placeImages);
		PlaceHeart heart = getHeart(member, place);
		Comment comment = getComment(member, place);

		given(journeyRepository.findById(any())).willReturn(Optional.of(journey));
		given(placeRepository.findAllByJourneyId(any())).willReturn(List.of(place));
		given(placeImageRepository.findAllByPlaceId(any())).willReturn(placeImages);
		given(commentRepository.findAllByPlaceId(any())).willReturn(List.of(comment));
		given(placeHeartRepository.findAllByPlaceId(any())).willReturn(List.of(heart));

		//when
		boolean deletePlace = placeService.deleteAll(1L, "memberId");

		//then
		verify(placeImageRepository, times(2)).delete(any());
		verify(placeHeartRepository, times(1)).deleteAll(any());
		verify(commentRepository, times(1)).deleteAll(any());
		verify(placeRepository, times(1)).deleteAll(any());
	}

	@DisplayName("05_01. deleteAll fail not found journey")
	@Test
	public void test_05_01() {
		//given
		given(journeyRepository.findById(any())).willReturn(Optional.empty());

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.deleteAll(1L, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_JOURNEY);
	}

	@DisplayName("05_02. deleteAll fail mismatch memberId")
	@Test
	public void test_05_02() {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);

		given(journeyRepository.findById(any())).willReturn(Optional.of(journey));

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.deleteAll(1L, "mismatch"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.MISMATCH_MEMBER_ID);
	}

	@DisplayName("05_03. deleteAll fail delete nothing")
	@Test
	public void test_05_03() {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);

		given(journeyRepository.findById(any())).willReturn(Optional.of(journey));
		given(placeRepository.findAllByJourneyId(any())).willReturn(List.of());

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.deleteAll(1L, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.DELETED_NOTING);
	}

	@DisplayName("06_00. updatePlace success")
	@Test
	public void test_06_00() throws IOException {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		Place place = getPlace(journey);
		PlaceImage placeImage = getPlaceImage(place, "url save");
		PlaceImage placeImage1 = getPlaceImage(place, "url save");
		List<PlaceImage> placeImages = new ArrayList<>(List.of(placeImage, placeImage1));
		place.setPlaceImages(placeImages);

		given(placeRepository.findById(any())).willReturn(Optional.of(place));
		given(placeImageRepository.findAllByPlaceId(1L)).willReturn(placeImages);

		given(awsS3Service.uploadFiles(any())).willReturn(List.of("url update1", "url update2"));

		given(placeImageRepository.save(any())).willReturn(placeImage);

		//when
		List<MultipartFile> multipartFile = getMultipartFiles();

		UpdateRequest request = getUpdateRequest(journey);
		Response response = placeService.updatePlace(multipartFile, request, member.getId());

		//then
		verify(placeImageRepository, times(2)).delete(any());
		verify(placeImageRepository, times(2)).save(any());
	}

	@DisplayName("06_01. updatePlace fail not found place")
	@Test
	public void test_06_01() throws IOException {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		given(placeRepository.findById(any())).willReturn(Optional.empty());

		//when
		List<MultipartFile> multipartFile = getMultipartFiles();

		UpdateRequest request = getUpdateRequest(journey);
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.updatePlace(multipartFile, request, member.getId()));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_PLACE);
	}

	@DisplayName("06_02. updatePlace fail mismatch memberId")
	@Test
	public void test_06_02() throws IOException {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		Place place = getPlace(journey);

		given(placeRepository.findById(any())).willReturn(Optional.of(place));

		//when
		List<MultipartFile> multipartFile = getMultipartFiles();

		UpdateRequest request = getUpdateRequest(journey);
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.updatePlace(multipartFile, request, "mismatch"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.MISMATCH_MEMBER_ID);
	}

	private static Member getMember() {
		return Member.builder().id("memberId").build();
	}

	private static Journey getJourney(Member member) {
		return Journey.builder().id(1L).member(member).build();
	}

	private static Place getPlace(Journey journey) {
		return Place.builder()
			.id(1L)
			.latitude(1.0)
			.longitude(1.0)
			.title("test title")
			.text("test text")
			.addressName("test addressName")
			.region1("test region1")
			.region2("test region2")
			.region3("test region3")
			.region4("test region4")
			.placeTime(LocalDateTime.now())
			.placeCategory(PlaceCategoryType.ECT)
			.placeName("test placeName")
			.placeImages(List.of())
			.journey(journey)
			.build();
	}

	private static PlaceImage getPlaceImage(Place place, String url) {
		PlaceImage placeImage = PlaceImage.builder()
			.place(place)
			.imageUrl(url)
			.build();
		placeImage.setId(1L);
		return placeImage;
	}


	private static PlaceHeart getHeart(Member member, Place place) {
		return PlaceHeart.builder().member(member).place(place).build();
	}


	private Comment getComment(Member member, Place place) {
		Comment comment = Comment.builder().place(place).member(member).text("test comment")
			.build();
		comment.setId(1L);
		return comment;
	}

	private static List<MultipartFile> getMultipartFiles() throws IOException {
		List<MultipartFile> multipartFile = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			String file = String.format("%d.png", i);
			FileInputStream fis = new FileInputStream("src/main/resources/testImages/" + file);
			multipartFile.add(new MockMultipartFile(String.format("%d", i), file, "png", fis));
		}
		return multipartFile;
	}

	private static CreateRequest getCreateRequest(Journey journey) {
		return CreateRequest.builder()
			.latitude(1.0)
			.longitude(1.0)
			.title("create title")
			.text("create text")
			.addressName("create addressName")
			.region1("create region1")
			.region2("create region2")
			.region3("create region3")
			.region4("create region4")
			.placeTime(LocalDateTime.now())
			.placeCategory("기타")
			.placeName("create placeName")
			.journeyId(journey.getId())
			.build();
	}

	private static UpdateRequest getUpdateRequest(Journey journey) {
		return UpdateRequest.builder()
			.placeId(1L)
			.latitude(1.0)
			.longitude(1.0)
			.title("update title")
			.text("update text")
			.addressName("update addressName")
			.region1("update region1")
			.region2("update region2")
			.region3("update region3")
			.region4("update region4")
			.placeTime(LocalDateTime.now())
			.placeCategory("기타")
			.placeName("update placeName")
			.journeyId(journey.getId())
			.build();
	}
}