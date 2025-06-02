package org.diarymoodanalyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Wrapper Service class for Redis <br/>
 *
 * Simplify maintenance, and abstract direct API usage.
 */
@Service
@RequiredArgsConstructor
public class RedisService {

    // Using String based for human-readable on redis-cli and compatibility
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Save given `value` to redis as given `key` with `ttl`
     * @param key key
     * @param value value to save
     * @param ttl TTL, timeout
     * @param timeUnit time unit of TTL
     */
    public void saveWithTTL(String key, String value, long ttl, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
    }

    /**
     * Save given boolean `value` to redis as given `key` with `ttl`
     * @param key key
     * @param value value to save (boolean)
     * @param ttl TTL, timeout
     * @param timeUnit time unit of TTL
     */
    public void saveWithTTL(String key, boolean value, long ttl, TimeUnit timeUnit) {
        saveWithTTL(key, String.valueOf(value), ttl, timeUnit);
    }

    /**
     * Save given boolean `value` to redis as given `key` with `ttl`
     * @param key key
     * @param value value to save (long)
     * @param ttl TTL, timeout
     * @param timeUnit time unit of TTL
     */
    public void saveWithTTL(String key, long value, long ttl, TimeUnit timeUnit) {
        saveWithTTL(key, String.valueOf(value), ttl, timeUnit);
    }

    /**
     * Save given `value` to redis as given `key` with `ttl`
     * <br/>
     * TimUnit is `TimeUnit.MINUTES`
     * @param key key
     * @param value value to save
     * @param ttl TTL, timeout like N minutes
     */
    public void saveWithTTL(String key, String value, long ttl) {
        saveWithTTL(key, value, ttl, TimeUnit.MINUTES);
    }

    /**
     * Save given `value` to redis as given `key` with `ttl`
     * <br/>
     * TimUnit is `TimeUnit.MINUTES`
     * @param key key
     * @param value value to save
     * @param ttl TTL, timeout like N minutes
     */
    public void saveWithTTL(String key, boolean value, long ttl) {
        saveWithTTL(key, value, ttl, TimeUnit.MINUTES);
    }

    /**
     * Save given `value` to redis as given `key` with `ttl`
     * <br/>
     * TimUnit is `TimeUnit.MINUTES`
     * @param key key
     * @param value value to save
     * @param ttl TTL, timeout like N minutes
     */
    public void saveWithTTL(String key, long value, long ttl) {
        saveWithTTL(key, value, ttl, TimeUnit.MINUTES);
    }

    /**
     * Save given `value` to redis as given `key`
     * @param key key
     * @param value value to save
     */
    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Save given `value` to redis as given `key`
     * @param key key
     * @param value value to save
     */
    public void save(String key, boolean value) {
        save(key, String.valueOf(value));
    }

    /**
     * Save given `value` to redis as given `key`
     * @param key key
     * @param value value to save
     */
    public void save(String key, long value) {
        save(key, String.valueOf(value));
    }

    /**
     * Increment integer value stored as string under `key` by one with TTL
     * <br/>
     * When the increment is first, set TTL to `ttl` and `unit`
     * @param key key
     * @param ttl TTL, timeout
     * @param timeUnit time unit of TTL
     */
    public void incrementWithTTL(String key, long ttl, TimeUnit timeUnit) {
        Long count = redisTemplate.opsForValue().increment(key);

        // If incremented count is 1, set TTL
        if(count != null && count == 1) {
            redisTemplate.expire(key, ttl, timeUnit);
        }
    }

    /**
     * Increment integer value stored as string under `key` by one with TTL
     * <br/>
     * When the increment is first, set TTL to `ttl`
     * @param key key
     * @param ttl TTL, timeout as minutes
     */
    public void incrementWithTTL(String key, long ttl) {
        incrementWithTTL(key, ttl, TimeUnit.MINUTES);
    }

    /**
     * Returns the string `value` of a `key`
     * If key is not valid, return null
     * @param key key of value will be returned
     * @return string value of a key
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Returns the string value of a key. <br/>
     * If key is not valid, throw `NullPointerException`
     * @param key key of value will be returned
     * @param message detail message to be used in `NullPointerException`
     * @return string value of a key
     */
    public String get(String key, String message) {
        return Objects.requireNonNull(get(key), message);
    }

    /**
     * Returns the string value of a key and delete the key. <br/>
     * If key is not valid, return null
     * @param key key of value will be returned
     * @return string value of a key
     */
    public String getAndDelete(String key) {
        return redisTemplate.opsForValue().getAndDelete(key);
    }

    /**
     * Returns the string value of a key and delete the key. <br/>
     * If key is not valid, throw `NullPointerException`
     * @param key key of value will be returned
     * @param message detail message to be used in `NullPointerException`
     * @return string value of a key
     */
    public String getAndDelete(String key, String message) {
        return Objects.requireNonNull(getAndDelete(key), message);
    }

    /**
     * Returns the string value of a key as boolean <br/>
     * If key does not exist, return false
     * @param key key of value will be returned
     * @return if value is true, return true, otherwise false
     */
    public boolean getFlag(String key) {
        String value = get(key);

        return Boolean.parseBoolean(value);
    }

    /**
     * Returns the boolean value of a key and delete the key. <br/>
     * If key is not valid, return null
     * @param key key of value will be returned
     * @return string value of a key
     */
    public boolean getFlagAndDelete(String key) {
        String value = getAndDelete(key);

        return Boolean.parseBoolean(value);
    }

    /**
     * Returns the string value of a key as long <br/>
     * If key does not exist, return false
     * @param key key of value will be returned
     * @return value as long
     * @throws IllegalArgumentException when key does not exist or failed to parse value
     */
    public long getLong(String key) throws IllegalArgumentException {
        String value = redisTemplate.opsForValue().get(key);

        if(value == null) {
            throw new IllegalArgumentException("The key does not exist : " + key);
        }

        return Long.parseLong(value);
    }

    /**
     * Delete `value` of a `key`
     * @param key key of value will be deleted
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
