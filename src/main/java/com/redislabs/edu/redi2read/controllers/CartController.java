package com.redislabs.edu.redi2read.controllers;

import com.redislabs.edu.redi2read.models.Cart;
import com.redislabs.edu.redi2read.models.CartItem;
import com.redislabs.edu.redi2read.services.CartService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/carts" )
public class CartController {

    private final CartService cartService;

    public CartController( CartService cartService ) {
        this.cartService = cartService;
    }

    @GetMapping( "/{id}" )
    public Cart getById( @PathVariable( "id" ) String cartId ) {
        return cartService.getById( cartId );
    }

    @PostMapping( "/{id}" )
    public void addToCart( @PathVariable( "id" ) String cartId,
                           @RequestBody CartItem cartItem ) {
        cartService.addToCart( cartId, cartItem );
    }

    @DeleteMapping( "/{id}" )
    public void removeFromCart( @PathVariable( "id" ) String cartId, @RequestBody String isbn ) {
        cartService.removeFromCart( cartId, isbn );
    }

    @PostMapping( "/{id}/checkout" )
    public void checkout( @PathVariable( "id" ) String cartId ) {
        cartService.checkOut( cartId );
    }
}
