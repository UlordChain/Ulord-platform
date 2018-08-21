/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * springboot Redis
 *
 * @author chenxin
 * @since 2018-08-10
 */
@SuppressWarnings("unchecked")
@Component
public class RedisUtil {

	@SuppressWarnings("rawtypes")
	@Autowired
	private StringRedisTemplate redisTemplate;

	public void remove(final String... keys) {
		for (String key : keys) {
			remove(key);
		}
	}

	public Set<String> getKeys(final String pattern) {
		return redisTemplate.keys(pattern);
	}


//	public void removePattern(final String pattern) {
//		Set<Serializable> keys = redisTemplate.keys(pattern);
//		if (keys.size() > 0)
//			redisTemplate.delete(keys);
//	}


	public void remove(final String key) {
		if (exists(key)) {
			redisTemplate.delete(key);
		}
	}


	public boolean exists(final String key) {
		return redisTemplate.hasKey(key);
	}


	public Object get(final String key) {
		Object result = null;
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
		result = operations.get(key);
		return result;
	}


	public boolean set(final String key, String value) {
		boolean result = false;
		try {
			ValueOperations<String, String> operations = redisTemplate.opsForValue();
			operations.set(key, value);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	public boolean set(final String key, String value, Long expireTime) {
		boolean result = false;
		try {
			ValueOperations<String, String> operations = redisTemplate.opsForValue();
			operations.set(key, value);
			redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}