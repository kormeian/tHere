package onde.there.journey.service.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import onde.there.domain.Journey;
import onde.there.domain.JourneyBookmark;
import onde.there.domain.JourneyTheme;
import onde.there.domain.Member;
import onde.there.domain.type.JourneyThemeType;
import onde.there.domain.type.RegionType;
import onde.there.dto.journy.JourneyBookmarkDto.JourneyBookmarkPageResponse;
import onde.there.journey.exception.JourneyErrorCode;
import onde.there.journey.exception.JourneyException;
import onde.there.journey.repository.JourneyBookmarkRepository;
import onde.there.journey.repository.JourneyBookmarkRepositoryImpl;
import onde.there.journey.repository.JourneyRepository;
import onde.there.journey.service.JourneyBookmarkService;
import onde.there.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class JourneyBookmarkServiceTest {

	@Mock
	private JourneyBookmarkRepository journeyBookmarkRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private JourneyRepository journeyRepository;
	@Mock
	private JourneyBookmarkRepositoryImpl journeyBookmarkRepositoryImpl;
	@InjectMocks
	private JourneyBookmarkService journeyBookmarkService;


	@DisplayName("01_00. create journey bookmark success")
	@Test
	public void test_01_00() {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		JourneyBookmark journeyBookmark = getJourneyBookmark(member, journey);

		given(memberRepository.findById(any())).willReturn(Optional.of(member));
		given(journeyRepository.findById(any())).willReturn(Optional.of(journey));
		given(journeyBookmarkRepository.existsByMemberIdAndJourneyId(anyString(), any()))
			.willReturn(false);
		given(journeyBookmarkRepository.save(any()))
			.willReturn(journeyBookmark);
		ArgumentCaptor<JourneyBookmark> journeyBookmarkCaptor = ArgumentCaptor.forClass(
			JourneyBookmark.class);

		//when
		journeyBookmarkService.createBookmark(1L, "memberId");

		//then
		verify(journeyBookmarkRepository, times(1)).save(journeyBookmarkCaptor.capture());
		assertEquals(journeyBookmarkCaptor.getValue().getJourney().getTitle(), "Title Test");
	}

	@DisplayName("01_01. create journey bookmark fail not found member")
	@Test
	public void test_01_01() {
		//given
		given(memberRepository.findById(any())).willReturn(Optional.empty());

		//when
		JourneyException journeyException = assertThrows(JourneyException.class,
			() -> journeyBookmarkService.createBookmark(1L, "memberId"));

		//then
		assertEquals(journeyException.getErrorCode(), JourneyErrorCode.NOT_FOUND_MEMBER);
	}

	@DisplayName("01_02. create journey bookmark fail not found journey")
	@Test
	public void test_01_02() {
		//given
		Member member = getMember();

		given(memberRepository.findById(any())).willReturn(Optional.of(member));
		given(journeyRepository.findById(any())).willReturn(Optional.empty());

		//when
		JourneyException journeyException = assertThrows(JourneyException.class,
			() -> journeyBookmarkService.createBookmark(1L, "memberId"));

		//then
		assertEquals(journeyException.getErrorCode(), JourneyErrorCode.NOT_FOUND_JOURNEY);
	}

	@DisplayName("01_03. create journey bookmark fail already added bookmark")
	@Test
	public void test_01_03() {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);

		given(memberRepository.findById(any())).willReturn(Optional.of(member));
		given(journeyRepository.findById(any())).willReturn(Optional.of(journey));
		given(journeyBookmarkRepository.existsByMemberIdAndJourneyId(anyString(), any()))
			.willReturn(true);

		//when
		JourneyException journeyException = assertThrows(JourneyException.class,
			() -> journeyBookmarkService.createBookmark(1L, "memberId"));

		//then
		assertEquals(journeyException.getErrorCode(), JourneyErrorCode.ALREADY_ADDED_BOOKMARK);
	}

	@DisplayName("02_00. delete bookmark success")
	@Test
	public void test_02_00() {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		JourneyBookmark journeyBookmark = getJourneyBookmark(member, journey);

		given(journeyBookmarkRepository.findByMemberIdAndJourneyId(anyString(), any()))
			.willReturn(Optional.of(journeyBookmark));

		//when
		journeyBookmarkService.deleteBookmark(1L, "memberId");

		//then
		verify(journeyBookmarkRepository, times(1)).delete(any());
	}

	@DisplayName("02_01. delete bookmark fail not found bookmakr")
	@Test
	public void test_02_01() {
		//given
		given(journeyBookmarkRepository.findByMemberIdAndJourneyId(anyString(), any()))
			.willReturn(Optional.empty());

		//when
		JourneyException journeyException = assertThrows(JourneyException.class,
			() -> journeyBookmarkService.deleteBookmark(1L, "memberId"));

		//then
		assertEquals(journeyException.getErrorCode(), JourneyErrorCode.NOT_FOUND_BOOKMARK);
	}

	@DisplayName("03_00. get bookmark list success")
	@Test
	public void test_03_00() {
		//given
		Member member = getMember();
		Journey journey = getJourney(member);
		Journey journey1 = getJourney(member);
		journey1.setId(2L);
		Journey journey2 = getJourney(member);
		journey2.setId(3L);
		JourneyBookmark journeyBookmark = getJourneyBookmark(member, journey);
		JourneyBookmark journeyBookmark1 = getJourneyBookmark(member, journey1);
		JourneyBookmark journeyBookmark2 = getJourneyBookmark(member, journey2);

		List<JourneyBookmark> journeyBookmarks =
			List.of(journeyBookmark, journeyBookmark1, journeyBookmark2);

		Page<JourneyBookmark> page = new PageImpl<>(journeyBookmarks);

		given(memberRepository.existsById(any())).willReturn(true);
		given(journeyBookmarkRepositoryImpl.getBookmarkPage(any(), any()))
			.willReturn(page);

		//when
		Page<JourneyBookmarkPageResponse> journeyBookmarkServiceBookmarkList = journeyBookmarkService
			.getBookmarkList("memberId", PageRequest.of(0, 3));

		//then
		assertEquals((int) journeyBookmarkServiceBookmarkList.get().count(),
			3);
	}

	@DisplayName("03_01. get bookmark list fail")
	@Test
	public void test_03_01() {
		//given
		given(memberRepository.existsById(any())).willReturn(false);

		//when
		JourneyException journeyException = assertThrows(JourneyException.class,
			() -> journeyBookmarkService
				.getBookmarkList("memberId", PageRequest.of(0, 3)));

		//then
		assertEquals(journeyException.getErrorCode(), JourneyErrorCode.NOT_FOUND_MEMBER);
	}


	private static JourneyBookmark getJourneyBookmark(Member member, Journey journey) {
		return JourneyBookmark.builder().id(1L).journey(journey).member(member).build();
	}

	private static Journey getJourney(Member member) {
		return Journey.builder()
			.id(1L)
			.member(member)
			.title("Title Test")
			.startDate(LocalDate.now().minusDays(1))
			.endDate(LocalDate.now())
			.journeyThumbnailUrl("test url")
			.disclosure("public")
			.introductionText("test 소개 글")
			.journeyThemes(
				List.of(JourneyTheme.builder().journeyThemeName(JourneyThemeType.CULTURE).build()))
			.numberOfPeople(7)
			.region(RegionType.SEOUL)
			.build();
	}

	private static Member getMember() {
		return Member.builder().id("memberId").build();
	}
}