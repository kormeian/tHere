package onde.there.place.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import onde.there.domain.Member;
import onde.there.domain.Place;
import onde.there.domain.PlaceHeart;
import onde.there.member.repository.MemberRepository;
import onde.there.place.exception.PlaceErrorCode;
import onde.there.place.exception.PlaceException;
import onde.there.place.repository.PlaceHeartRepository;
import onde.there.place.repository.PlaceHeartSchedulingRepository;
import onde.there.place.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class PlaceHeartServiceTest {

	@Autowired
	private PlaceHeartService placeHeartService;
	@Autowired
	private PlaceHeartRepository placeHeartRepository;
	@Autowired
	private PlaceRepository placeRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PlaceHeartSchedulingRepository placeHeartSchedulingRepository;


	@DisplayName("01_00. heart success -> Place.placeHeartSum < 1000")
	@Test
	public void test_01_00() {
		//given
		Place place = placeRepository.save(Place.builder()
			.placeHeartCount(10)
			.build());
		Long placeId = place.getId();

		Member member = new Member();
		member.setId("testMember");
		memberRepository.save(member);

		//when
		placeHeartService.heart(placeId, "testMember");

		//then
		assertEquals(place.getPlaceHeartCount(), 11);
		assertTrue(placeHeartRepository.existsByPlaceIdAndMemberId(place.getId(), "testMember"));
	}

	@DisplayName("01_01. heart success -> Place.placeHeartSum >= 1000")
	@Test
	public void test_01_01() {
		//given
		Place place = placeRepository.save(Place.builder()
			.placeHeartCount(10000)
			.build());
		Long placeId = place.getId();

		Member member = new Member();
		member.setId("testMember");
		memberRepository.save(member);

		//when
		placeHeartService.heart(placeId, "testMember");

		//then
		assertEquals(place.getPlaceHeartCount(), 10000);
		assertTrue(placeHeartRepository.existsByPlaceIdAndMemberId(place.getId(), "testMember"));
		assertEquals(placeHeartSchedulingRepository.findAll().size(), 1);
	}

	@DisplayName("01_02. heart fail not found place")
	@Test
	public void test_01_02() {
		//given

		Member member = new Member();
		member.setId("testMember");
		memberRepository.save(member);

		//when
		PlaceException exception = assertThrows(PlaceException.class,
			() -> placeHeartService.heart(100000000L, "testMember"));

		//then
		assertEquals(exception.getErrorCode(), PlaceErrorCode.NOT_FOUND_PLACE);
	}

	@DisplayName("01_03. heart fail not found member")
	@Test
	public void test_01_03() {
		//given
		Place place = placeRepository.save(Place.builder()
			.placeHeartCount(10)
			.build());

		//when
		PlaceException exception = assertThrows(PlaceException.class,
			() -> placeHeartService.heart(place.getId(), "testMember"));

		//then
		assertEquals(exception.getErrorCode(), PlaceErrorCode.NOT_FOUND_MEMBER);
	}

	@DisplayName("01_04. heart fail -> already hearted")
	@Test
	public void test_01_04() {
		//given
		Place place = placeRepository.save(Place.builder()
			.placeHeartCount(10)
			.build());
		Long placeId = place.getId();

		Member member = new Member();
		member.setId("testMember");
		memberRepository.save(member);

		placeHeartRepository.save(PlaceHeart.builder().place(place).member(member).build());

		//when
		PlaceException exception = assertThrows(PlaceException.class,
			() -> placeHeartService.heart(placeId, "testMember"));

		//then
		assertEquals(exception.getErrorCode(), PlaceErrorCode.ALREADY_HEARTED);
	}

	@DisplayName("02_00. unheart success -> Place.placeHeartSum < 1000")
	@Test
	public void test_02_00() {
		//given
		Place place = placeRepository.save(Place.builder()
			.placeHeartCount(10)
			.build());
		Long placeId = place.getId();

		Member member = new Member();
		member.setId("testMember");
		memberRepository.save(member);

		placeHeartRepository.save(PlaceHeart.builder()
			.place(place)
			.member(member)
			.build());

		//when
		placeHeartService.unHeart(placeId, "testMember");

		//then
		assertEquals(place.getPlaceHeartCount(), 9);
		assertFalse(placeHeartRepository.existsByPlaceIdAndMemberId(place.getId(), "testMember"));
	}

	@DisplayName("02_01. unheart success -> Place.placeHeartSum >= 1000")
	@Test
	public void test_02_01() {
		//given
		Place place = placeRepository.save(Place.builder()
			.placeHeartCount(10000)
			.build());
		Long placeId = place.getId();

		Member member = new Member();
		member.setId("testMember");
		memberRepository.save(member);

		placeHeartRepository.save(PlaceHeart.builder()
			.place(place)
			.member(member)
			.build());

		//when
		placeHeartService.unHeart(placeId, "testMember");

		//then
		assertEquals(place.getPlaceHeartCount(), 10000);
		assertFalse(placeHeartRepository.existsByPlaceIdAndMemberId(place.getId(), "testMember"));
	}

	@DisplayName("02_02. unheart fail not found place")
	@Test
	public void test_02_02() {
		//given

		//when
		PlaceException exception = assertThrows(PlaceException.class,
			() -> placeHeartService.unHeart(100000000000L, "testMember"));

		//then
		assertEquals(exception.getErrorCode(), PlaceErrorCode.NOT_FOUND_PLACE);
	}

	@DisplayName("02_03. unheart fail not found member")
	@Test
	public void test_02_03() {
		//given
		Place place = placeRepository.save(Place.builder()
			.placeHeartCount(10)
			.build());

		//when
		PlaceException exception = assertThrows(PlaceException.class,
			() -> placeHeartService.unHeart(place.getId(), "testMember"));

		//then
		assertEquals(exception.getErrorCode(), PlaceErrorCode.NOT_FOUND_MEMBER);
	}

	@DisplayName("02_04. unheart fail -> already unHearted")
	@Test
	public void test_02_04() {
		//given
		Place place = placeRepository.save(Place.builder()
			.placeHeartCount(10)
			.build());
		Long placeId = place.getId();

		Member member = new Member();
		member.setId("testMember");
		memberRepository.save(member);

		//when
		PlaceException exception = assertThrows(PlaceException.class,
			() -> placeHeartService.unHeart(placeId, "testMember"));

		//then
		assertEquals(exception.getErrorCode(), PlaceErrorCode.ALREADY_UN_HEARTED);
	}
}