package one.ulord.upaas.ucwallet.service.base.config;


import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import one.ulord.upaas.ucwallet.service.base.common.AddressItem;
import one.ulord.upaas.ucwallet.service.base.common.TransactionItem;
import one.ulord.upaas.ucwallet.service.base.common.TransactionPos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, AddressItem> addressItemRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, AddressItem> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonRedisSerializer<>(AddressItem.class));
        template.setDefaultSerializer(new FastJsonRedisSerializer<>(AddressItem.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, TransactionItem> transactionItemRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, TransactionItem> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonRedisSerializer<>(TransactionItem.class));
        template.setDefaultSerializer(new FastJsonRedisSerializer<>(TransactionItem.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, TransactionPos> transactionPosRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, TransactionPos> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonRedisSerializer<>(TransactionPos.class));
        template.setDefaultSerializer(new FastJsonRedisSerializer<>(TransactionPos.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, String> stringItemRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonRedisSerializer<>(String.class));
        template.setDefaultSerializer(new FastJsonRedisSerializer<>(String.class));
        return template;
    }
}
