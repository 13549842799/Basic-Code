package com.cyz.basic.config;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.cyz.basic.pojo.RedisOwnProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@Configuration
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(value={RedisProperties.class, RedisOwnProperties.class})//当配置这个注解时，引入的配置了@ConfigurationProperties注解的类无需添加@Component注解也会被注入到IOC中
public class RedisConfig extends CachingConfigurerSupport {
	
	@Autowired
	private RedisProperties properties;
	
	@Autowired
	private RedisOwnProperties own;

	@Bean
    public LettuceConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(properties.getHost(), properties.getPort());
		configuration.setPassword(RedisPassword.of(properties.getPassword()));
		configuration.setDatabase(properties.getDatabase());
		LettuceConnectionFactory f = new LettuceConnectionFactory(configuration);
        return f;
    }
	
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		
		 Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = this.cretateJackson2JsonRedisSerializer();
		
		RedisTemplate<String, Object> redis = new RedisTemplate<>();
		redis.setConnectionFactory(factory);
		redis.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		
		StringRedisSerializer serializer = new StringRedisSerializer();
		redis.setKeySerializer(serializer);
		redis.setHashKeySerializer(serializer);
		redis.setValueSerializer(jackson2JsonRedisSerializer);
		return redis;
	}
	
	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
		StringRedisTemplate redis = new StringRedisTemplate(factory);
		StringRedisSerializer serializer = new StringRedisSerializer();
		redis.setKeySerializer(serializer);
		return redis;
	}
	
	

	/*@Override
	public CacheErrorHandler errorHandler() {
		
		return new IgnoreExceptionCacheErrorHandler
	}*/

	
	
    /**
     * 默认的key生成cacheName::key
     */
	@Bean
	@Override
	public CacheManager cacheManager() {
        
        RedisCacheConfiguration config = this.getRedisCacheConfigurationWithTtl(0);

        RedisCacheManager cacheManager = RedisCacheManager.builder(this.redisConnectionFactory())
        		.cacheDefaults(config).withInitialCacheConfigurations(this.getInitialCaches()).build();
        return cacheManager;
	}
	
	private Jackson2JsonRedisSerializer<Object> cretateJackson2JsonRedisSerializer() {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        //下面代码解决LocalDateTime序列化与反序列化不一致问题
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 解决jackson2无法反序列化LocalDateTime的问题
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
	}
	
	/**
	 * 因为Spring cache不支持配置过期时间，所以手动添加,其中key为Spring Cache注解中value的值
	 * @param cs
	 */
	private Map<String, RedisCacheConfiguration> getInitialCaches() {
		Map<String, RedisCacheConfiguration> initialCaches = new LinkedHashMap<>();
		if (own.getExpireMap().size() == 0) {
			return initialCaches;
		}
		for (Map.Entry<String, Long> entry : own.getExpireMap().entrySet()) {
			initialCaches.put(entry.getKey(), this.getRedisCacheConfigurationWithTtl(entry.getValue().intValue()));
		}
		return initialCaches;
	}
	
	private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = this.cretateJackson2JsonRedisSerializer();
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues().entryTtl(Duration.ofSeconds(seconds));
 
        return redisCacheConfiguration;
    }


}
