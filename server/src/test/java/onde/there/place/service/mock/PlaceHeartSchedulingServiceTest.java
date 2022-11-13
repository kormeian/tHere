package onde.there.place.service.mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import onde.there.domain.Place;
import onde.there.place.repository.PlaceHeartRepository;
import onde.there.place.repository.PlaceRepository;
import onde.there.place.service.PlaceHeartSchedulingService;
import onde.there.place.utils.RedisServiceForPlace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceHeartSchedulingServiceTest {

	@Mock
	private PlaceHeartRepository placeHeartRepository;

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private RedisServiceForPlace<Long> redisService;

	@InjectMocks
	private PlaceHeartSchedulingService placeHeartSchedulingService;

	private static final String PLACE_ID_KEY = "test placeId";

	@DisplayName("01_00. culPlaceHeartCount success")
	@Test
	public void test_01_00() {
		//given
		given(redisService.hasKey(anyString())).willReturn(true);
		given(redisService.get(anyString())).willReturn(List.of(1L));
		given(placeRepository.findById(any())).willReturn(
			Optional.of(Place.builder().id(1L).build()));
		given(placeHeartRepository.countByPlaceId(any())).willReturn(1000L);
		given(placeRepository.save(any())).willReturn(
			Place.builder().id(1L).placeHeartCount(1234L).build());
		given(redisService.delete(anyString())).willReturn(true);

		//when
		placeHeartSchedulingService.culPlaceHeartCount();

		//then
	}

	@DisplayName("01_01. culPlaceHeartCount success")
	@Test
	public void test_01_01() {
		//given
		given(redisService.hasKey(anyString())).willReturn(false);

		//when
		placeHeartSchedulingService.culPlaceHeartCount();

		//then
	}


	@DisplayName("01_02. culPlaceHeartCount success")
	@Test
	public void test_01_02() {
		//given
		given(redisService.hasKey(anyString())).willReturn(true);
		given(redisService.get(anyString())).willReturn(new ArrayList<>());
		given(redisService.delete(anyString())).willReturn(true);

		//when
		placeHeartSchedulingService.culPlaceHeartCount();

		//then
	}
}