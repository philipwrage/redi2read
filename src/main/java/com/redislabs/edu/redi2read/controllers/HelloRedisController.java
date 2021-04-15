package com.redislabs.edu.redi2read.controllers;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.AbstractMap;
import java.util.Map;

@RestController
@RequestMapping("api/redis")
public class HelloRedisController {

    private static final String STRING_KEY_PREFIX = "redi2read:strings:";
    private final RedisTemplate<String, String> redisTemplate;

    public HelloRedisController( RedisTemplate<String, String> redisTemplate ) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping( "strings" )
    @ResponseStatus( HttpStatus.CREATED )
    public Map.Entry<String, String> setString( @RequestBody Map.Entry<String, String> kvp ) {
        redisTemplate.opsForValue().set( STRING_KEY_PREFIX + kvp.getKey(), kvp.getValue() );
        return kvp;
    }

    @GetMapping( "strings/{key}" )
    public Map.Entry<String, String> getString( @PathVariable( "key" ) String key ) {
        String value = redisTemplate.opsForValue().get( STRING_KEY_PREFIX + key );
        if ( value == null ) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "key not found" );
        }
        return new AbstractMap.SimpleEntry<>( key, value );
    }

}
