package com.redislabs.edu.redi2read.models;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import java.util.Set;

@Data
@Builder
public class Cart {
    private String id;
    private String userId;

    @Singular
    private Set<CartItem> cartItems;

    public Integer count() {
        return getCartItems().size();
    }

    public Double getTotal() {
        return getCartItems().stream()
            .mapToDouble( cartItem -> cartItem.getPrice() * cartItem.getQuantity() )
            .sum();
    }

}
