package com.redislabs.edu.redi2read.boot;

import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.models.Cart;
import com.redislabs.edu.redi2read.models.CartItem;
import com.redislabs.edu.redi2read.models.User;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CartRepository;
import com.redislabs.edu.redi2read.services.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

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
        log.debug( "Starting CommandLineRunner: {}", getClass().getName() );
        if ( cartRepository.count() == 0 ) {
            Random random = new Random();

            IntStream.range( 0, numberOfCarts )
                .forEach( n -> {
                    String userId = redisTemplate.opsForSet().randomMember( User.class.getName() );
                    Set<Book> books = getRandomBooks( bookRepository, 7 );

                    Cart cart = Cart.builder().userId( userId ).build();
                    cart.setCartItems( getCartItemsForBooks(books) );
                    cartRepository.save( cart );

                    if ( random.nextBoolean() ) {
                        cartService.checkOut( cart.getId() );
                    }
                } );
            log.debug( "Created carts." );
        }
    }

    private Set<Book> getRandomBooks( BookRepository bookRepository, int max ) {
        Random random = new Random();
        int count = random.nextInt( max ) + 1;
        Set<Book> books = new HashSet<>();
        IntStream.range( 0, count ).forEach( n -> {
            String bookId = redisTemplate.opsForSet().randomMember( Book.class.getName() );
            bookRepository.findById( bookId ).ifPresent( books::add );
        } );
        return books;
    }

    private Set<CartItem> getCartItemsForBooks( Set<Book> books ) {
        Set<CartItem> cartItems = new HashSet<>();

        books.forEach( book -> {
            CartItem cartItem = CartItem.builder()
                .isbn( book.getId() )
                .price( book.getPrice() )
                .quantity( 1L )
                .build();
            cartItems.add( cartItem );
        } );
        return cartItems;
    }
}
