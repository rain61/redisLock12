package config;


import com.arvato.service.loyalty.util.BaseJedisLockAdapter;
import com.arvato.service.loyalty.util.DefaultJedisLockAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;



@Configuration
public class JedisLockAdapterConfig {

    @Autowired
    RedisTemplate redisTemplate;

    @Bean
    public BaseJedisLockAdapter defaultJedisLockAdapter(){

        return new DefaultJedisLockAdapter(redisTemplate);
    }
}
