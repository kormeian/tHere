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
import onde.there.place.utils.RedisServiceForPlace;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaceHeartService {

	private final PlaceHeartRepository placeHeartRepository;
	private final PlaceRepository placeRepository;
	private final MemberRepository memberRepository;
	private final RedisServiceForPlace<Long> redisService;
	private static final String PLACE_ID_KEY = "placeHeartSchedulingPlaceId";

	public boolean heart(Long placeId, String memberId) {
		boolean heart = true;
		log.info("heart : 장소 좋아요 메소드 시작 (장소 아이디 : " + placeId + ") (맴버 아이디 : " + memberId + ")");
		Place place = checkPlace(placeId);

		Member member = checkMember(memberId);

		checkPlaceHeart(place, member, heart);

		placeHeartUpdateRole(placeId, place, heart);

		log.info("heart : 장소 좋아요 메소드 완료 (장소 아이디 : " + placeId + ") (맴버 아이디 : " + memberId + ")");
		return true;
	}

	public boolean unHeart(Long placeId, String memberId) {
		boolean unHeart = false;
		log.info(
			"unHeart : 장소 좋아요 취소 메소드 시작 (장소 아이디 : " + placeId + ") (맴버 아이디 : " + memberId + ")");
		Place place = checkPlace(placeId);

		Member member = checkMember(memberId);

		checkPlaceHeart(place, member, unHeart);

		placeHeartUpdateRole(placeId, place, unHeart);

		log.info(
			"unHeart : 장소 좋아요 취소 메소드 완료 (장소 아이디 : " + placeId + ") (맴버 아이디 : " + memberId + ")");
		return true;
	}

	private Place checkPlace(Long placeId) {
		return placeRepository.findById(placeId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_PLACE));
	}

	private Member checkMember(String memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_MEMBER));
	}

	private void checkPlaceHeart(Place place, Member member, boolean heartOrUnHeart) {
		if (heartOrUnHeart) {
			if (placeHeartRepository.existsByPlaceIdAndMemberId(place.getId(), member.getId())) {
				throw new PlaceException(PlaceErrorCode.ALREADY_HEARTED);
			}

			placeHeartRepository.save(PlaceHeart.builder()
				.place(place)
				.member(member)
				.build());
		} else {
			PlaceHeart placeHeart = placeHeartRepository.findByPlaceAndMember(place, member)
				.orElseThrow(() -> new PlaceException(PlaceErrorCode.ALREADY_UN_HEARTED));

			placeHeartRepository.delete(placeHeart);
		}
	}

	private void placeHeartUpdateRole(Long placeId, Place place, boolean heartOrUnHeart) {
		log.info("placeHeartUpdateRole : 장소 좋아요 갯수 업데이트 메소드 시작! (장소 아이디 : " + placeId + ")");
		if (place.getPlaceHeartCount() >= 1000) {
			addSchedule(placeId);
			log.info(
				"좋아요 갯수 1000개 이상 -> placeHeartUpdateRole : 장소 좋아요 갯수 업데이트 메소드 취소 -> 스케줄링으로 저장 완료! (장소 아이디 : "
					+ placeId + ")");
		} else {
			place.setPlaceHeartCount(place.getPlaceHeartCount() + (heartOrUnHeart ? 1 : -1));
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