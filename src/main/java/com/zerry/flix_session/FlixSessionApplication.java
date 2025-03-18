package com.zerry.flix_session;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * SessionServiceApplication
 * 세션 서버의 메인 클래스입니다.
 */
@SpringBootApplication
@EnableRedisRepositories
public class FlixSessionApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlixSessionApplication.class, args);
	}

}
