package com.redislabs.edu.redi2read.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Builder
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
@RedisHash
public class Role {
    @Id
    private String id;

    @Indexed
    private String name;
}
