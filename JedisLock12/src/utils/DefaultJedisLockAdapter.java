package utils;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

public class DefaultJedisLockAdapter extends BaseJedisLockAdapter {

    RedisTemplate redisTemplate;

    public DefaultJedisLockAdapter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public Boolean setNX(String key, String value) {
        return (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.setNX(key.getBytes(), value.getBytes());
            }
        });
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Object getSet(String key, String value) {
        return redisTemplate.opsForValue().getAndSet(key,value);
    }

    @Override
    public void del(String key) {
        redisTemplate.delete(key);
    }
}
