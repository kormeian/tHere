package onde.there.place.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisServiceForPlace<T> {

	private final RedisTemplate<String, T> redisTemplate;

	public void setPlaceId(String key, T placeId) {
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(placeId.getClass()));
		redisTemplate.opsForSet().add(key, placeId);
	}

	public List<T> get(String key) {
		Set<T> set = redisTemplate.opsForSet().members(key);
		if (set == null) {
			return new ArrayList<>();
		}
		return new ArrayList<>(set);
	}

	public boolean delete(String key) {
		return Boolean.TRUE.equals(redisTemplate.delete(key));
	}

	public boolean hasKey(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}
}
