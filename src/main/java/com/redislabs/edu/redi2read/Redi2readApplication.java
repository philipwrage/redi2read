package com.redislabs.edu.redi2read;

import com.redislabs.edu.redi2read.boot.BookRatingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableConfigurationProperties( { BookRatingProperties.class } )
public class Redi2readApplication {

	public static void main( String[] args ) {
		SpringApplication.run( Redi2readApplication.class, args );
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate( RedisConnectionFactory redisConnectionFactory ) {
		RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory( redisConnectionFactory );
		return redisTemplate;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
