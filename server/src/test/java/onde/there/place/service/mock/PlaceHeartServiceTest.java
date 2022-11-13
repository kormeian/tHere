package onde.there.place.service.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import onde.there.domain.Member;
import onde.there.domain.Place;
import onde.there.domain.PlaceHeart;
import onde.there.member.repository.MemberRepository;
import onde.there.place.exception.PlaceErrorCode;
import onde.there.place.exception.PlaceException;
import onde.there.place.repository.PlaceHeartRepository;
import onde.there.place.repository.PlaceRepository;
import onde.there.place.service.PlaceHeartService;
import onde.there.utils.RedisServiceForSoftDelete;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceHeartServiceTest {

	@InjectMocks
	private PlaceHeartService placeHeartService;

	@Mock
	private PlaceHeartRepository placeHeartRepository;

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private RedisServiceForSoftDelete<Long> redisService;

	@DisplayName("01_00. heart success placeHeartCount < 1000")
	@Test
	public void test_01_00() {
		//given
		given(placeRepository.findById(any())).willReturn(
			Optional.of(Place.builder().id(1L).build()));
		given(memberRepository.findById(any())).willReturn(
			Optional.of(Member.builder().id("memberId").build()));
		given(placeHeartRepository.existsByPlaceIdAndMemberId(any(), anyString())).willReturn(
			false);

		//when
		boolean heart = placeHeartService.heart(1L, "memberId");

		//then
		assertTrue(heart);
		verify(placeRepository, times(1)).save(any());
	}

	@DisplayName("01_01. heart success placeHeartCount >= 1000")
	@Test
	public void test_01_01() {
		//given
		given(placeRepository.findById(any())).willReturn(
			Optional.of(Place.builder().id(1L).placeHeartCount(10001L).build()));
		given(memberRepository.findById(any())).willReturn(
			Optional.of(Member.builder().id("memberId").build()));
		given(placeHeartRepository.existsByPlaceIdAndMemberId(any(), anyString())).willReturn(
			false);

		//when
		boolean heart = placeHeartService.heart(1L, "memberId");

		//then
		verify(redisService, times(1)).setPlaceId(anyString(), any());
		assertTrue(heart);
	}

	@DisplayName("01_02. heart fail not found place")
	@Test
	public void test_01_02() {
		//given
		given(placeRepository.findById(any())).willReturn(
			Optional.empty());

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeHeartService.heart(1L, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_PLACE);
	}

	@DisplayName("01_03. heart fail not found member")
	@Test
	public void test_01_03() {
		//given
		given(placeRepository.findById(any())).willReturn(
			Optional.of(Place.builder().id(1L).build()));

		given(memberRepository.findById(any())).willReturn(
			Optional.empty());

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeHeartService.heart(1L, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_MEMBER);
	}

	@DisplayName("01_04. heart fail already hearted")
	@Test
	public void test_01_04() {
		//given
		given(placeRepository.findById(any())).willReturn(
			Optional.of(Place.builder().id(1L).build()));
		given(memberRepository.findById(any())).willReturn(
			Optional.of(Member.builder().id("memberId").build()));

		given(placeHeartRepository.existsByPlaceIdAndMemberId(any(), anyString())).willReturn(
			true);

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeHeartService.heart(1L, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.ALREADY_HEARTED);
	}


	@DisplayName("02_00. unheart success placeHeartCount < 1000")
	@Test
	public void test_02_00() {
		//given
		Place place = Place.builder().id(1L).build();
		Member member = Member.builder().id("memberId").build();
		given(placeRepository.findById(any())).willReturn(
			Optional.of(place));
		given(memberRepository.findById(any())).willReturn(
			Optional.of(member));
		given(placeHeartRepository.findByPlaceAndMember(any(), any())).willReturn(
			Optional.of(PlaceHeart.builder().place(place).member(member).build()));

		//when
		boolean unheart = placeHeartService.unHeart(1L, "memberId");

		//then
		assertTrue(unheart);
		verify(placeHeartRepository, times(1)).delete(any());
	}

	@DisplayName("02_01. unHeart success placeHeartCount >= 1000")
	@Test
	public void test_02_01() {
		//given
		Place place = Place.builder().id(1L).placeHeartCount(100000L).build();
		Member member = Member.builder().id("memberId").build();
		given(placeRepository.findById(any())).willReturn(
			Optional.of(place));
		given(memberRepository.findById(any())).willReturn(
			Optional.of(member));
		given(placeHeartRepository.findByPlaceAndMember(any(), any())).willReturn(
			Optional.of(PlaceHeart.builder().place(place).member(member).build()));

		//when
		boolean unheart = placeHeartService.unHeart(1L, "memberId");

		//then
		assertTrue(unheart);
		verify(placeHeartRepository, times(1)).delete(any());
		verify(redisService, times(1)).setPlaceId(anyString(), any());
	}

	@DisplayName("02_02. unHeart fail not found place")
	@Test
	public void test_02_02() {
		//given
		given(placeRepository.findById(any())).willReturn(
			Optional.empty());

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeHeartService.unHeart(1L, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_PLACE);
	}

	@DisplayName("02_03. unHeart fail not found member")
	@Test
	public void test_02_03() {
		//given
		given(placeRepository.findById(any())).willReturn(
			Optional.of(Place.builder().id(1L).build()));

		given(memberRepository.findById(any())).willReturn(
			Optional.empty());

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeHeartService.unHeart(1L, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.NOT_FOUND_MEMBER);
	}

	@DisplayName("02_04. unHeart fail already unHearted")
	@Test
	public void test_02_04() {
		//given
		given(placeRepository.findById(any())).willReturn(
			Optional.of(Place.builder().id(1L).build()));
		given(memberRepository.findById(any())).willReturn(
			Optional.of(Member.builder().id("memberId").build()));

		given(placeHeartRepository.findByPlaceAndMember(any(), any()))
			.willReturn(Optional.empty());

		//when
		PlaceException placeException = assertThrows(PlaceException.class,
			() -> placeHeartService.unHeart(1L, "memberId"));

		//then
		assertEquals(placeException.getErrorCode(), PlaceErrorCode.ALREADY_UN_HEARTED);
	}

}