package com.redislabs.edu.redi2read.boot;

import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CartRepository;
import com.redislabs.edu.redi2read.services.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Order( 5 )
@Slf4j
public class CreateCarts implements CommandLineRunner {

    private final RedisTemplate<String,String> redisTemplate;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;

    @Value( "${app.carts.number-of-carts}" )
    private Integer numberOfCarts;

    public CreateCarts( RedisTemplate<String, String> redisTemplate,
                        CartService cartService,
                        CartRepository cartRepository,
                        BookRepository bookRepository ) {
        this.redisTemplate = redisTemplate;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run( String... args ) throws Exception {

    }
}
