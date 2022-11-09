package onde.there.place.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.domain.Member;
import onde.there.domain.Place;
import onde.there.domain.PlaceHeart;
import onde.there.member.repository.MemberRepository;
import onde.there.place.exception.PlaceErrorCode;
import onde.there.place.exception.PlaceException;
import onde.there.place.repository.PlaceHeartRepository;
import onde.there.place.repository.PlaceRepository;
import onde.there.place.utils.RedisServiceForPlaceHeart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaceHeartService {

	private final PlaceHeartRepository placeHeartRepository;
	private final PlaceRepository placeRepository;
	private final MemberRepository memberRepository;
	private final RedisServiceForPlaceHeart<Long> redisService;
	private static final String PLACE_ID_KEY = "placeId";

	@Transactional
	public boolean heart(Long placeId, String memberId) {
		log.info("heart : 장소 좋아요 메소드 시작 (장소 아이디 : " + placeId + ") (맴버 아이디 : " + memberId + ")");
		Place place = checkPlace(placeId);

		Member member = checkMember(memberId);

		if (placeHeartRepository.existsByPlaceIdAndMemberId(placeId, memberId)) {
			throw new PlaceException(PlaceErrorCode.ALREADY_HEARTED);
		}

		placeHeartRepository.save(PlaceHeart.builder()
			.place(place)
			.member(member)
			.build());

		placeHeartUpdateRole(placeId, place, true);

		log.info("heart : 장소 좋아요 메소드 완료 (장소 아이디 : " + placeId + ") (맴버 아이디 : " + memberId + ")");
		return true;
	}

	@Transactional
	public boolean unHeart(Long placeId, String memberId) {
		log.info(
			"unHeart : 장소 좋아요 취소 메소드 시작 (장소 아이디 : " + placeId + ") (맴버 아이디 : " + memberId + ")");
		Place place = checkPlace(placeId);

		Member member = checkMember(memberId);

		PlaceHeart placeHeart = placeHeartRepository.findByPlaceAndMember(place, member)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.ALREADY_UN_HEARTED));

		placeHeartRepository.delete(placeHeart);

		placeHeartUpdateRole(placeId, place, false);

		log.info("unHeart : 장소 좋아요 취소 메소드 완료 (장소 아이디 : " + placeId + ") (맴버 아이디 : " + memberId + ")");
		return true;
	}

	private Place checkPlace(Long placeId) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_PLACE));
		return place;
	}

	private Member checkMember(String memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_MEMBER));
		return member;
	}

	private void placeHeartUpdateRole(Long placeId, Place place, boolean plusOrMinus) {
		log.info("placeHeartUpdateRole : 장소 좋아요 갯수 업데이트 메소드 시작! (장소 아이디 : " + placeId + ")");
		if (place.getPlaceHeartCount() >= 1000) {
			addSchedule(placeId);
			log.info(
				"좋아요 갯수 1000개 이상 -> placeHeartUpdateRole : 장소 좋아요 갯수 업데이트 메소드 취소 -> 스케줄링으로 저장 완료! (장소 아이디 : "
					+ placeId + ")");
		} else {
			place.setPlaceHeartCount(place.getPlaceHeartCount() + (plusOrMinus ? 1 : -1));
			placeRepository.save(place);
			log.info("좋아요 갯수 1000개 미만 -> placeHeartUpdateRole : 장소 좋아요 갯수 업데이트 메소드 완료! (장소 아이디 : "
				+ placeId + ")");
		}
	}

	private void addSchedule(Long placeId) {
		log.info("addSchedule : 장소 좋아요 스케쥴링 저장 시작(장소 아이디 : " + placeId + ")");
		redisService.setPlaceId(PLACE_ID_KEY, placeId);
		log.info("addSchedule : 장소 좋아요 스케쥴링 저장 완료(장소 아이디 : " + placeId + ")");
	}
}