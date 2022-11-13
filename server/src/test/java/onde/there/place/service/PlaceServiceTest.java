package onde.there.place.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import onde.there.utils.RedisServiceForSoftDelete;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
class PlaceServiceTest {

	@Autowired
	private PlaceService placeService;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private PlaceImageRepository placeImageRepository;

	@Autowired
	private JourneyRepository journeyRepository;

	@Autowired
	private PlaceHeartRepository placeHeartRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private AwsS3Service awsS3Service;

	@Autowired
	private RedisServiceForSoftDelete<Long> redisService;

	private static final String PLACE_ID = "placeId";

	@Test
	void 장소_저장() throws IOException {
		//given
		List<MultipartFile> multipartFile = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			String file = String.format("%d.png", i);
			FileInputStream fis = new FileInputStream("src/main/resources/testImages/" + file);
			multipartFile.add(new MockMultipartFile(String.format("%d", i), file, "png", fis));
		}
		Journey journey = journeyRepository.save(
			Journey.builder()
				.member(
					memberRepository.save(Member.builder()
						.id("memberId")
						.build()))
				.build()
		);
		PlaceDto.CreateRequest request = CreateRequest.builder()
			.latitude(123.0)
			.longitude(234.0)
			.title("test")
			.text("테스트 중 입니다.")
			.addressName("경기도 김포시 고촌읍 어쩌구 저쩌구")
			.region1("경기도")
			.region2("김포시")
			.region3("고촌읍")
			.region4("어쩌구 저쩌구")
			.placeTime(LocalDateTime.now())
			.journeyId(journey.getId())
			.placeCategory(PlaceCategoryType.NATURE.getDescription())
			.placeName("어딘가")
			.build();
		//when
		Response place = placeService.createPlace(multipartFile, request, "memberId");
		//then
		assertEquals(place.getJourneyId(), journey.getId());
		assertEquals(3, placeImageRepository.findAllByPlaceId(place.getPlaceId()).size());
	}

	@Test
	void 장소_저장_작성자_불일치() throws IOException {
		//given
		List<MultipartFile> multipartFile = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			String file = String.format("%d.png", i);
			FileInputStream fis = new FileInputStream("src/main/resources/testImages/" + file);
			multipartFile.add(new MockMultipartFile(String.format("%d", i), file, "png", fis));
		}
		Journey journey = journeyRepository.save(
			Journey.builder()
				.member(
					memberRepository.save(Member.builder()
						.id("memberId")
						.build()))
				.build()
		);
		PlaceDto.CreateRequest request = CreateRequest.builder()
			.latitude(123.0)
			.longitude(234.0)
			.title("test")
			.text("테스트 중 입니다.")
			.addressName("경기도 김포시 고촌읍 어쩌구 저쩌구")
			.region1("경기도")
			.region2("김포시")
			.region3("고촌읍")
			.region4("어쩌구 저쩌구")
			.placeTime(LocalDateTime.now())
			.journeyId(journey.getId())
			.placeCategory(PlaceCategoryType.NATURE.getDescription())
			.placeName("어딘가")
			.build();
		//when
		PlaceException placeException = assertThrows(PlaceException.class, () ->
			placeService.createPlace(multipartFile, request, "ㅂㅈㄷㄱ"));
		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.MISMATCH_MEMBER_ID);
	}

	@Test
	void 장소_저장_에러_여행아이디() throws IOException {
		//given
		List<MultipartFile> multipartFile = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			String file = String.format("%d.png", i);
			FileInputStream fis = new FileInputStream("src/main/resources/testImages/" + file);
			multipartFile.add(new MockMultipartFile(String.format("%d", i), file, "png", fis));
		}
		PlaceDto.CreateRequest request = CreateRequest.builder()
			.latitude(123.0)
			.longitude(234.0)
			.title("test")
			.text("테스트 중 입니다.")
			.addressName("경기도 김포시 고촌읍 어쩌구 저쩌구")
			.region1("경기도")
			.region2("김포시")
			.region3("고촌읍")
			.region4("어쩌구 저쩌구")
			.placeTime(LocalDateTime.now())
			.journeyId(1000000L)
			.placeCategory(PlaceCategoryType.NATURE.getDescription())
			.placeName("어딘가")
			.build();
		//when
		//then
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.createPlace(multipartFile, request, "memberId"));
		assertEquals(PlaceErrorCode.NOT_FOUND_JOURNEY.getDescription(),
			placeException.getErrorMessage());
	}

	@Test
	void 장소_저장_에러_카테고리() throws IOException {
		//given
		List<MultipartFile> multipartFile = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			String file = String.format("%d.png", i);
			FileInputStream fis = new FileInputStream("src/main/resources/testImages/" + file);
			multipartFile.add(new MockMultipartFile(String.format("%d", i), file, "png", fis));
		}
		Journey journey = journeyRepository.save(Journey.builder()
			.member(memberRepository.save(Member.builder()
				.id("memberId").build()))
			.build());
		PlaceDto.CreateRequest request = CreateRequest.builder()
			.latitude(123.0)
			.longitude(234.0)
			.title("test")
			.text("테스트 중 입니다.")
			.addressName("경기도 김포시 고촌읍 어쩌구 저쩌구")
			.region1("경기도")
			.region2("김포시")
			.region3("고촌읍")
			.region4("어쩌구 저쩌구")
			.placeTime(LocalDateTime.now())
			.journeyId(journey.getId())
			.placeCategory("asdfa")
			.placeName("어딘가")
			.build();
		//when
		//then

		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.createPlace(multipartFile, request, "memberId"));

		assertEquals(PlaceErrorCode.MISMATCH_PLACE_CATEGORY_TYPE.getDescription(),
			placeException.getErrorMessage());
	}

	@DisplayName("01_00. getPlace success")
	@Test
	public void test_01_00() {
		//given
		Journey journey = journeyRepository.save(Journey.builder().build());
		Place place = Place.builder()
			.text("테스트 장소 본문")
			.title("테스트 장소 제목")
			.placeCategory(PlaceCategoryType.ECT)
			.journey(journey)
			.build();

		Place placeHeart2022 = Place.builder()
			.text("테스트 장소 본문")
			.title("테스트 장소 제목")
			.placeCategory(PlaceCategoryType.ECT)
			.placeHeartCount(2022)
			.journey(journey)
			.build();

		Place savePlace = placeRepository.save(place);
		Place savePlaceHeart2022 = placeRepository.save(placeHeart2022);

		savePlace.setPlaceImages(List.of(
			PlaceImage.builder().imageUrl("url1").build(),
			PlaceImage.builder().imageUrl("url2").build()));

		savePlaceHeart2022.setPlaceImages(List.of(
			PlaceImage.builder().imageUrl("url1").build(),
			PlaceImage.builder().imageUrl("url2").build()));

		//when
		PlaceDto.Response placeDto = placeService.getPlace(savePlace.getId());
		PlaceDto.Response placeDtoHeart2022 = placeService.getPlace(savePlaceHeart2022.getId());

		//then
		assertEquals(placeDto.getPlaceId(), savePlace.getId());

		assertEquals(placeDto.getText(), "테스트 장소 본문");
		assertEquals(placeDto.getTitle(), "테스트 장소 제목");
		assertEquals(placeDto.getImageUrls().size(), 2);
		assertEquals(placeDto.getImageUrls().get(0), "url1");
		assertEquals(placeDtoHeart2022.getPlaceHeartCount(), "2k");

	}

	@DisplayName("01_01. getPlace fail not found place")
	@Test
	public void test_01_01() {
		//given
		//when
		PlaceException exception = assertThrows(PlaceException.class,
			() -> placeService.getPlace(1000000L));
		//then
		assertEquals(exception.getErrorCode(), PlaceErrorCode.NOT_FOUND_PLACE);
	}

	@DisplayName("02_00_0. list success login and no hearted")
	@Test
	public void test_02_00_0() {
		//given
		Journey journey = journeyRepository.save(Journey.builder().build());
		List<PlaceImage> placeImages = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			placeImages = new ArrayList<>();
			Place save = placeRepository.save(Place.builder()
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
				.journey(journey)
				.build());

			for (int k = 0; k < 2; k++) {
				placeImages.add(placeImageRepository.save(PlaceImage.builder()
					.place(save)
					.imageUrl("url" + k)
					.build()));
			}
			save.setPlaceImages(placeImages);
		}

		//when
		List<PlaceDto.Response> list = placeService.placeListOfJourney(journey.getId(), "memberId");
		for (Response r : list) {
			System.out.println(r);
		}
		System.out.println(list.get(0).getImageUrls());
		//then
		assertEquals(list.size(), 3);
		assertEquals(list.get(0).getJourneyId(), list.get(1).getJourneyId());
		assertEquals(list.get(0).getImageUrls().get(0), "url0");
		assertEquals(list.get(1).getImageUrls().get(0), "url0");
		assertEquals(list.get(2).getImageUrls().get(0), "url0");
		assertFalse(list.get(0).isHeartedCheck());
	}

	@DisplayName("02_00_1. list success login hearted")
	@Test
	public void test_02_00_1() {
		//given
		Member member = new Member();
		member.setId("memberId");
		memberRepository.save(member);

		Journey journey = journeyRepository.save(Journey.builder().build());
		List<PlaceImage> placeImages = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			placeImages.add(placeImageRepository.save(PlaceImage.builder()
				.imageUrl("url" + i)
				.build()));
		}

		for (int i = 0; i < 3; i++) {
			Place save = placeRepository.save(Place.builder()
				.journey(journey)
				.placeCategory(PlaceCategoryType.ECT)
				.placeTime(LocalDateTime.now().plusSeconds(i))
				.build());
			save.setPlaceImages(placeImages);
			placeHeartRepository.save(PlaceHeart.builder()
				.place(save)
				.member(member)
				.build());
		}

		//when
		List<PlaceDto.Response> list = placeService.placeListOfJourney(journey.getId(), "memberId");

		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		//then
		assertEquals(list.size(), 3);
		assertEquals(list.get(0).getJourneyId(), list.get(1).getJourneyId());
		assertEquals(list.get(0).getImageUrls().get(0), "url0");
		assertEquals(list.get(1).getImageUrls().get(0), "url0");
		assertEquals(list.get(2).getImageUrls().get(0), "url0");
		assertTrue(list.get(0).isHeartedCheck());
	}

	@DisplayName("02_00_2. list success no login ")
	@Test
	public void test_02_00_2() {
		//given
		Journey journey = journeyRepository.save(Journey.builder().build());
		List<PlaceImage> placeImages = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			placeImages.add(placeImageRepository.save(PlaceImage.builder()
				.imageUrl("url" + i)
				.build()));
		}

		for (int i = 0; i < 3; i++) {
			Place save = placeRepository.save(Place.builder()
				.journey(journey)
				.placeCategory(PlaceCategoryType.ECT)
				.placeTime(LocalDateTime.now().plusSeconds(i))
				.build());
			save.setPlaceImages(placeImages);
		}

		//when
		List<PlaceDto.Response> list = placeService.placeListOfJourney(journey.getId(), "");

		//then
		assertEquals(list.size(), 3);
		assertEquals(list.get(0).getJourneyId(), list.get(1).getJourneyId());
		assertEquals(list.get(0).getImageUrls().get(0), "url0");
		assertEquals(list.get(1).getImageUrls().get(0), "url0");
		assertEquals(list.get(2).getImageUrls().get(0), "url0");
		assertFalse(list.get(0).isHeartedCheck());
	}


	@DisplayName("02_01. list fail not found journey")
	@Test
	public void test_02_01() {
		//given

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.placeListOfJourney(1000000L, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_JOURNEY);
		assertEquals(placeException.getErrorMessage(),
			PlaceErrorCode.NOT_FOUND_JOURNEY.getDescription());
	}

	@DisplayName("03_00. delete success")
	@Test
	public void test_03_00() {
		//given

		Place save = placeRepository.save(Place.builder()
			.journey(journeyRepository.save(Journey.builder()
				.member(memberRepository.save(Member.builder()
					.id("memberId")
					.build()))
				.build()))
			.build());

		//when
		boolean delete = placeService.deletePlace(save.getId(), "memberId");
		//then
		assertTrue(save.isDeleted());
		assertEquals(redisService.get(PLACE_ID).get(0), save.getId());
		redisService.delete(PLACE_ID);
	}

	@DisplayName("03_01. delete fail not found place")
	@Test
	public void test_03_01() {
		//given

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.deletePlace(100011L, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_PLACE);
	}

	@DisplayName("03_02. delete fail member mismatch")
	@Test
	public void test_03_02() {
		//given
		Place save = placeRepository.save(Place.builder()
			.journey(journeyRepository.save(Journey.builder()
				.member(memberRepository.save(Member.builder()
					.id("memberId")
					.build()))
				.build()))
			.build());

		List<Long> placeImageId = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PlaceImage p = placeImageRepository.save(PlaceImage.builder()
				.place(save)
				.imageUrl("url")
				.build());
			placeImageId.add(p.getId());
		}

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.deletePlace(save.getId(), "asdf"));
		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.MISMATCH_MEMBER_ID);
	}

	@DisplayName("04_00. deleteAll success")
	@Test
	public void test_04_00() {
		//given
		Journey save = journeyRepository.save(
			Journey.builder()
				.member(memberRepository.save(Member.builder()
					.id("memberId")
					.build()))
				.build());
		journeyRepository.save(save);

		Place save1 = placeRepository.save(Place.builder().journey(save).build());
		Place save2 = placeRepository.save(Place.builder().journey(save).build());
		Place save3 = placeRepository.save(Place.builder().journey(save).build());

		//when
		boolean result = placeService.deleteAll(save.getId(), "memberId");

		//then
		assertTrue(result);
		assertTrue(save1.isDeleted());
		assertEquals(redisService.get(PLACE_ID).get(0), save1.getId());
		assertTrue(save2.isDeleted());
		assertEquals(redisService.get(PLACE_ID).get(1), save2.getId());
		assertTrue(save3.isDeleted());
		assertEquals(redisService.get(PLACE_ID).get(2), save3.getId());
		redisService.delete(PLACE_ID);
	}

	@DisplayName("04_01. deleteAll fail not deleted")
	@Test
	public void test_04_01() {
		//given
		Journey save = journeyRepository.save(
			Journey.builder()
				.member(memberRepository.save(Member.builder()
					.id("memberId")
					.build()))
				.build());
		journeyRepository.save(save);

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.deleteAll(save.getId(), "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.DELETED_NOTING);
	}

	@DisplayName("04_02. deleteAll fail not found journeyId")
	@Test
	public void test_04_02() {
		//given
		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.deleteAll(100000L, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_JOURNEY);
	}

	@DisplayName("04_03. deleteAll success")
	@Test
	public void test_04_03() {
		//given
		Journey save = journeyRepository.save(
			Journey.builder()
				.member(memberRepository.save(Member.builder()
					.id("memberId")
					.build()))
				.build());
		journeyRepository.save(save);

		List<Long> placeImageIdes = new ArrayList<>();

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.deleteAll(save.getId(), "asdf"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.MISMATCH_MEMBER_ID);
	}

	@DisplayName("05_00. updatePlace success")
	@Test
	public void test_05_00() throws IOException {
		//given
		Journey saveJourney = journeyRepository.save(Journey.builder()
			.member(memberRepository.save(Member.builder()
				.id("memberId")
				.build()))
			.build());

		Place savePlace = placeRepository.save(Place.builder()
			.latitude(1.0)
			.longitude(1.0)
			.title("title test")
			.text("text test")
			.addressName("total address")
			.region1("place name1")
			.region2("place name2")
			.region3("place name3")
			.region4("place name4")
			.placeName("place name")
			.placeTime(LocalDateTime.now())
			.placeCategory(PlaceCategoryType.ECT)
			.journey(saveJourney)
			.placeHeartCount(0)
			.build());

		placeImageRepository.save(PlaceImage.builder()
			.place(savePlace)
			.imageUrl("image url test1")
			.build());

		placeImageRepository.save(PlaceImage.builder()
			.place(savePlace)
			.imageUrl("image url test2")
			.build());

		List<MultipartFile> multipartFile = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			String file = String.format("%d.png", i);
			FileInputStream fis = new FileInputStream("src/main/resources/testImages/" + file);
			multipartFile.add(new MockMultipartFile(String.format("%d", i), file, "png", fis));
		}

		//when
		UpdateRequest updateRequest = UpdateRequest.builder()
			.placeId(savePlace.getId())
			.latitude(1.0)
			.longitude(1.0)
			.title("title test update")
			.text("text test update")
			.addressName("total address update")
			.region1("place name1 update")
			.region2("place name2 update")
			.region3("place name3 update")
			.region4("place name4 update")
			.placeName("place name update")
			.placeTime(LocalDateTime.now().minusDays(1))
			.placeCategory("자연")
			.journeyId(saveJourney.getId())
			.build();

		Response response = placeService.updatePlace(multipartFile, updateRequest, "memberId");
		System.out.println(response.getImageUrls());
		//then
		assertEquals(response.getImageUrls().size(), 3);
		assertEquals(response.getPlaceId(), savePlace.getId());
		assertEquals(response.getText(), updateRequest.getText());
		assertEquals(response.getTitle(), updateRequest.getTitle());
		assertEquals(response.getAddressName(), updateRequest.getAddressName());
		assertEquals(response.getRegion1(), updateRequest.getRegion1());
		assertEquals(response.getRegion2(), updateRequest.getRegion2());
		assertEquals(response.getRegion3(), updateRequest.getRegion3());
		assertEquals(response.getRegion4(), updateRequest.getRegion4());
		assertEquals(response.getPlaceName(), updateRequest.getPlaceName());
		assertEquals(response.getPlaceCategory(), updateRequest.getPlaceCategory());
		assertEquals(response.getJourneyId(), saveJourney.getId());

		List<String> imageUrls = response.getImageUrls();
		for (String url : imageUrls) {
			awsS3Service.deleteFile(url);
		}
	}

	@DisplayName("05_01. updatePlace fail not found place")
	@Test
	public void test_05_01() throws IOException {
		//given
		Journey saveJourney = journeyRepository.save(Journey.builder().build());

		Place savePlace = Place.builder().id(1000000L).build();

		List<MultipartFile> multipartFile = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			String file = String.format("%d.png", i);
			FileInputStream fis = new FileInputStream("src/main/resources/testImages/" + file);
			multipartFile.add(new MockMultipartFile(String.format("%d", i), file, "png", fis));
		}

		//when
		UpdateRequest updateRequest = UpdateRequest.builder()
			.placeId(savePlace.getId())
			.latitude(1.0)
			.longitude(1.0)
			.title("title test update")
			.text("text test update")
			.addressName("total address update")
			.region1("place name1 update")
			.region2("place name2 update")
			.region3("place name3 update")
			.region4("place name4 update")
			.placeName("place name update")
			.placeTime(LocalDateTime.now().minusDays(1))
			.placeCategory("자연")
			.journeyId(saveJourney.getId())
			.build();

		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.updatePlace(multipartFile, updateRequest, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_PLACE);
	}

	@DisplayName("05_02. updatePlace fail category mismatch ")
	@Test
	public void test_05_02() throws IOException {
		//given
		Journey saveJourney = journeyRepository.save(Journey.builder()
			.member(memberRepository.save(Member.builder()
				.id("memberId")
				.build()))
			.build());

		Place savePlace =
			placeRepository.save(Place.builder()
				.latitude(1.0)
				.longitude(1.0)
				.title("title test")
				.text("text test")
				.addressName("total address")
				.region1("place name1")
				.region2("place name2")
				.region3("place name3")
				.region4("place name4")
				.placeName("place name")
				.placeTime(LocalDateTime.now())
				.placeCategory(PlaceCategoryType.ECT)
				.journey(saveJourney)
				.placeHeartCount(0)
				.build());

		placeImageRepository.save(PlaceImage.builder()
			.place(savePlace)
			.imageUrl("image url test1")
			.build());

		placeImageRepository.save(PlaceImage.builder()
			.place(savePlace)
			.imageUrl("image url test2")
			.build());

		List<MultipartFile> multipartFile = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			String file = String.format("%d.png", i);
			FileInputStream fis = new FileInputStream("src/main/resources/testImages/" + file);
			multipartFile.add(new MockMultipartFile(String.format("%d", i), file, "png", fis));
		}

		//when
		UpdateRequest updateRequest = UpdateRequest.builder()
			.placeId(savePlace.getId())
			.latitude(1.0)
			.longitude(1.0)
			.title("title test update")
			.text("text test update")
			.addressName("total address update")
			.region1("place name1 update")
			.region2("place name2 update")
			.region3("place name3 update")
			.region4("place name4 update")
			.placeName("place name update")
			.placeTime(LocalDateTime.now().minusDays(1))
			.placeCategory("자연dddd")
			.journeyId(saveJourney.getId())
			.build();

		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.updatePlace(multipartFile, updateRequest, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.MISMATCH_PLACE_CATEGORY_TYPE);
	}

	@DisplayName("05_03. updatePlace fail memberId mismatch")
	@Test
	public void test_05_03() throws IOException {
		//given
		Journey saveJourney = journeyRepository.save(Journey.builder()
			.member(memberRepository.save(Member.builder()
				.id("memberId")
				.build()))
			.build());

		Place savePlace = placeRepository.save(Place.builder()
			.latitude(1.0)
			.longitude(1.0)
			.title("title test")
			.text("text test")
			.addressName("total address")
			.region1("place name1")
			.region2("place name2")
			.region3("place name3")
			.region4("place name4")
			.placeName("place name")
			.placeTime(LocalDateTime.now())
			.placeCategory(PlaceCategoryType.ECT)
			.journey(saveJourney)
			.placeHeartCount(0)
			.build());

		List<MultipartFile> multipartFile = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			String file = String.format("%d.png", i);
			FileInputStream fis = new FileInputStream("src/main/resources/testImages/" + file);
			multipartFile.add(new MockMultipartFile(String.format("%d", i), file, "png", fis));
		}

		//when
		UpdateRequest updateRequest = UpdateRequest.builder()
			.placeId(savePlace.getId())
			.latitude(1.0)
			.longitude(1.0)
			.title("title test update")
			.text("text test update")
			.addressName("total address update")
			.region1("place name1 update")
			.region2("place name2 update")
			.region3("place name3 update")
			.region4("place name4 update")
			.placeName("place name update")
			.placeTime(LocalDateTime.now().minusDays(1))
			.placeCategory("자연")
			.journeyId(saveJourney.getId())
			.build();

		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeService.updatePlace(multipartFile, updateRequest, "asdf"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.MISMATCH_MEMBER_ID);
	}
}