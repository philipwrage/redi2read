package com.redislabs.edu.redi2read.services;

import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.models.Cart;
import com.redislabs.edu.redi2read.models.CartItem;
import com.redislabs.edu.redi2read.models.User;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CartRepository;
import com.redislabs.edu.redi2read.repositories.UserRepository;
import com.redislabs.modules.rejson.JReJSON;
import com.redislabs.modules.rejson.Path;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final JReJSON redisJson = new JReJSON();
    private final Path CART_ITEMS_JSON_PATH = Path.of( ".cartItems" );

    public CartService( CartRepository cartRepository,
                        BookRepository bookRepository,
                        UserRepository userRepository ) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public Cart getById( String id ) {
        return cartRepository.findById( id ).get();
    }

    public void addToCart( String cartId, CartItem cartItem ) {
        Optional<Book> optionalBook = bookRepository.findById( cartItem.getIsbn() );
        optionalBook.ifPresent( book -> {
            String cartKey = CartRepository.getKey( cartId );
            cartItem.setPrice( book.getPrice() );
            redisJson.arrAppend( cartKey, CART_ITEMS_JSON_PATH, cartItem );
        } );
    }

    public void removeFromCart( String cartId, String isbn ) {
        Optional<Cart> optionalCart = cartRepository.findById( cartId );
        optionalCart.ifPresent( cart -> {
            String cartKey = CartRepository.getKey( cart );
            List<CartItem> cartItems = new ArrayList<>( cart.getCartItems() );

            LongStream.range( 0, cartItems.size() )
                .filter( idx -> isbn.equals( cartItems.get( (int) idx ).getIsbn() ) )
                .findFirst()
                .ifPresent( index ->
                    redisJson.arrPop( cartKey, CartItem.class, CART_ITEMS_JSON_PATH, index )
                );
        } );
    }

    public void checkOut( String cartId ) {
        Optional<Cart> optionalCart = cartRepository.findById( cartId );

        optionalCart.ifPresent( cart -> {
            Optional<User> optionalUser = userRepository.findById( cart.getUserId() );

            optionalUser.ifPresent( user -> {
                cart.getCartItems().forEach( cartItem -> {
                    Optional<Book> optionalBook = bookRepository.findById( cartItem.getIsbn() );
                    optionalBook.ifPresent( user::addBook );
                } );
                userRepository.save( user );
            } );
        } );
    }

}
