package com.redislabs.edu.redi2read.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;
import javax.validation.constraints.NotNull;

@Data
@Builder
@RedisHash
public class BookRating {
    @Id
    private String id;

    @NotNull
    @Reference
    private User user;

    @NotNull
    @Reference
    private Book book;

    @NotNull
    private Integer rating;
}
