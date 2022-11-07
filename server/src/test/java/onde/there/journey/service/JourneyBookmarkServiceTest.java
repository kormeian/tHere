package onde.there.journey.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import onde.there.domain.Journey;
import onde.there.domain.JourneyBookmark;
import onde.there.domain.Member;
import onde.there.domain.type.RegionType;
import onde.there.dto.journy.JourneyBookmarkDto.JourneyBookmarkPageResponse;
import onde.there.journey.exception.JourneyErrorCode;
import onde.there.journey.exception.JourneyException;
import onde.there.journey.repository.JourneyBookmarkRepository;
import onde.there.journey.repository.JourneyRepository;
import onde.there.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class JourneyBookmarkServiceTest {

	@Autowired
	private JourneyBookmarkService journeyBookmarkService;
	@Autowired
	private JourneyRepository journeyRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private JourneyBookmarkRepository journeyBookmarkRepository;

	@Test
	void 북마크_저장() {
		//given
		Member member = memberRepository.save(new Member("1", "", "", ""));
		Journey journey = journeyRepository.save(new Journey());
		//when
		Long id = journeyBookmarkService.createBookmark(journey.getId(), member.getId());
		//then
		JourneyBookmark journeyBookmark = journeyBookmarkRepository.findById(id).orElseThrow();
		assertEquals(member.getId(), journeyBookmark.getMember().getId());
		assertEquals(journey.getId(), journeyBookmark.getJourney().getId());

	}

	@Test
	void 북마크_저장_에러_멤버아이디() {
		//given
		Member member = memberRepository.save(new Member("1", "", "", ""));
		Journey journey = journeyRepository.save(new Journey());
		//when
		JourneyException journeyException = assertThrows(JourneyException.class,
			() -> journeyBookmarkService.createBookmark(journey.getId(), "any member Id"));
		//then
		assertEquals(JourneyErrorCode.NOT_FOUND_MEMBER, journeyException.getErrorCode());
	}

	@Test
	void 북마크_저장_에러_여정아이디() {
		//given
		Member member = memberRepository.save(new Member("1", "", "", ""));
		Journey journey = journeyRepository.save(new Journey());
		//when
		JourneyException journeyException = assertThrows(JourneyException.class,
			() -> journeyBookmarkService.createBookmark(123L, member.getId()));
		//then
		assertEquals(JourneyErrorCode.NOT_FOUND_JOURNEY, journeyException.getErrorCode());
	}

	@Test
	void 북마크_삭제() {
		//given
		Member member = memberRepository.save(new Member("1", "", "", ""));
		Journey journey = journeyRepository.save(new Journey());
		JourneyBookmark journeyBookmark = journeyBookmarkRepository.save(JourneyBookmark.builder()
			.member(member)
			.journey(journey)
			.build());
		//when
		journeyBookmarkService.deleteBookmark(journey.getId(), member.getId());
		//then
		assertFalse(journeyBookmarkRepository.existsById(journeyBookmark.getId()));
	}

	@Test
	void 북마크_삭제_에러_북마크아이디() {
		//given
		Member member = memberRepository.save(new Member("1", "", "", ""));
		Journey journey = journeyRepository.save(new Journey());
		JourneyBookmark journeyBookmark = journeyBookmarkRepository.save(JourneyBookmark.builder()
			.member(member)
			.journey(journey)
			.build());
		//when
		System.out.println(journeyBookmarkRepository.count());
		JourneyException journeyException = assertThrows(JourneyException.class , () ->journeyBookmarkService.deleteBookmark(123L, member.getId()));
		//then
		assertEquals(JourneyErrorCode.NOT_FOUND_BOOKMARK, journeyException.getErrorCode());
	}

	@Test
	void 북마크_조회() {
		//given
		Member member = memberRepository.save(new Member("1", "", "", ""));
		Journey journey = journeyRepository.save(Journey.builder()
				.member(member)
				.title("TitleTest")
				.region(RegionType.BUSAN)
			.build());
		journeyBookmarkService.createBookmark(journey.getId(), member.getId());
		Journey journey2 = journeyRepository.save(Journey.builder()
			.member(member)
			.title("TitleTest2")
			.region(RegionType.CHUNGBUK)
			.build());
		journeyBookmarkService.createBookmark(journey2.getId(), member.getId());
		Pageable pageable = PageRequest.of(0, 3);
		//when
		Page<JourneyBookmarkPageResponse> responses =
			journeyBookmarkService.getBookmarkList(member.getId(), pageable);
		//then
		for (JourneyBookmarkPageResponse response : responses) {
			assertEquals(member.getId(), response.getMemberId());
		}
		assertEquals(2, responses.getTotalElements());
	}

	@Test
	void 북마크_조회_찜안했을경우() {
		//given
		Member member = memberRepository.save(new Member("1", "", "", ""));
		Pageable pageable = PageRequest.of(0, 3);
		//when
		Page<JourneyBookmarkPageResponse> responses =
			journeyBookmarkService.getBookmarkList(member.getId(), pageable);
		//then
		assertEquals(0, responses.getTotalElements());
	}

	@Test
	void 북마크_조회_에러_멤버아이디() {
	    //given
		Member member = memberRepository.save(new Member("1", "", "", ""));
		Journey journey = journeyRepository.save(Journey.builder()
			.member(member)
			.title("TitleTest")
			.region(RegionType.BUSAN)
			.build());
		journeyBookmarkService.createBookmark(journey.getId(), member.getId());
		Journey journey2 = journeyRepository.save(Journey.builder()
			.member(member)
			.title("TitleTest2")
			.region(RegionType.CHUNGBUK)
			.build());
		journeyBookmarkService.createBookmark(journey2.getId(), member.getId());
		Pageable pageable = PageRequest.of(0, 3);
		//when
		JourneyException journeyException = assertThrows(JourneyException.class, () -> journeyBookmarkService.getBookmarkList("any member Id", pageable));
	    //then
		assertEquals(JourneyErrorCode.NOT_FOUND_MEMBER, journeyException.getErrorCode());
	}
}