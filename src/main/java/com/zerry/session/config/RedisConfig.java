package com.zerry.session.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.zerry.session.model.Session;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Session> sessionRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Session> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer(
                Session.class.getClassLoader());
        template.setValueSerializer(serializer);

        return template;
    }

    @Bean
    public RedisTemplate<String, Object> objectRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer(
                Session.class.getClassLoader());
        template.setValueSerializer(serializer);

        return template;
    }
}
