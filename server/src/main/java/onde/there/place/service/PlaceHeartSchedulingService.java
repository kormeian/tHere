package onde.there.place.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.domain.Place;
import onde.there.place.repository.PlaceHeartRepository;
import onde.there.place.repository.PlaceRepository;
import onde.there.utils.RedisServiceForSoftDelete;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaceHeartSchedulingService {

	private final PlaceHeartRepository placeHeartRepository;
	private final PlaceRepository placeRepository;

	private final RedisServiceForSoftDelete<Long> redisService;
	private static final String PLACE_ID_KEY = "placeHeartChangedId";

	@Scheduled(cron = " 0 0 3 * * *")
	@Transactional
	public void culPlaceHeartCount() {
		log.info("culPlaceHeartSum : 저장된 모든 장소 스케줄링 시작!");
		if (!redisService.hasKey(PLACE_ID_KEY)) {
			log.info("culPlaceHeartCount : 저장된 placeId 없음 종료!");
			return;
		}

		List<Long> placeIdSchedules = redisService.get(PLACE_ID_KEY);

		for (Long placeId : placeIdSchedules) {
			log.info("장소 좋아요 스케쥴링 시작! (장소 아이디 : " + placeId + ")");
			Optional<Place> optionalPlace = placeRepository.findById(placeId);

			if (optionalPlace.isEmpty()) {
				log.info("장소 스케줄링 좋아요 업데이트 실패! (존재하지 않는 장소 아이디 " + placeId + ")");
				continue;
			}

			Place place = optionalPlace.get();

			Long updatePlaceHeartCount = placeHeartRepository.countByPlaceId(place.getId());

			if (updatePlaceHeartCount != place.getPlaceHeartCount()) {
				log.info("(업데이트 전 좋아요 갯수 : " + place.getPlaceHeartCount() + ")");
				place.setPlaceHeartCount(updatePlaceHeartCount);
				placeRepository.save(place);
				log.info("(업데이트 후 좋아요 갯수 : " + place.getPlaceHeartCount() + ")");
			} else {
				log.info("(좋아요 갯수 변화 없음)");
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			log.info("culPlaceHeartCount : 장소 좋아요 스케쥴링 완료! (장소 아이디 : " + placeId + ")");
		}

		log.info("culPlaceHeartCount : RedisKey placeId 삭제 시작!)");
		boolean delete = redisService.delete(PLACE_ID_KEY);
		log.info("culPlaceHeartCount : RedisKey placeId 삭제 완료 결과 : " + delete + ")");
	}
}
