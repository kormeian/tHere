package onde.there.journey.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.domain.Journey;
import onde.there.domain.JourneyBookmark;
import onde.there.domain.Member;
import onde.there.dto.journy.JourneyBookmarkDto.JourneyBookmarkPageResponse;
import onde.there.journey.exception.JourneyErrorCode;
import onde.there.journey.exception.JourneyException;
import onde.there.journey.repository.JourneyBookmarkRepository;
import onde.there.journey.repository.JourneyBookmarkRepositoryImpl;
import onde.there.journey.repository.JourneyRepository;
import onde.there.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JourneyBookmarkService {

	private final JourneyBookmarkRepository journeyBookmarkRepository;
	private final MemberRepository memberRepository;
	private final JourneyRepository journeyRepository;
	private final JourneyBookmarkRepositoryImpl journeyBookmarkRepositoryImpl;

	@Transactional
	public Long createBookmark(Long journeyId, String memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new JourneyException(
				JourneyErrorCode.NOT_FOUND_MEMBER));
		Journey journey = journeyRepository.findById(journeyId)
			.orElseThrow(() -> new JourneyException(JourneyErrorCode.NOT_FOUND_JOURNEY));
		if (journeyBookmarkRepository.existsByMemberIdAndJourneyId(memberId, journeyId)) {
			throw new JourneyException(JourneyErrorCode.ALREADY_ADDED_BOOKMARK);
		}
		JourneyBookmark journeyBookmark = journeyBookmarkRepository.save(JourneyBookmark.builder()
			.member(member)
			.journey(journey)
			.build());
		log.info(member.getId() + "님이 " + journey.getTitle() + " 여정을 북마크에 추가했습니다.");
		return journeyBookmark.getId();
	}

	@Transactional
	public void deleteBookmark(Long journeyId, String memberId) {
		JourneyBookmark journeyBookmark = journeyBookmarkRepository.findByMemberIdAndJourneyId(
			memberId, journeyId).orElseThrow(
			() -> new JourneyException(JourneyErrorCode.NOT_FOUND_BOOKMARK));
		journeyBookmarkRepository.delete(journeyBookmark);
		log.info("북마크 아이디 : " + journeyBookmark.getId() + "가 삭제 되었습니다.");
	}

	@Transactional(readOnly = true)
	public Page<JourneyBookmarkPageResponse> getBookmarkList(String memberId, Pageable pageable) {
		log.info("멤버 아이디 : " + memberId + " 북마크 조회 시작");
		if (!memberRepository.existsById(memberId)) {
			throw new JourneyException(JourneyErrorCode.NOT_FOUND_MEMBER);
		}

		return journeyBookmarkRepositoryImpl.getBookmarkPage(memberId, pageable)
			.map(JourneyBookmarkPageResponse::fromEntity);
	}
}
