package com.redislabs.edu.redi2read.models;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.lang.NonNull;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode( onlyExplicitlyIncluded = true )
@ToString( onlyExplicitlyIncluded = true )
@RedisHash
@JsonIgnoreProperties( value = { "password", "passwordConfirmation" }, allowSetters = true )
public class User {

    @Id
    @ToString.Include
    private String id;

    @NotNull
    @Size( min = 2, max = 48 )
    @ToString.Include
    private String name;

    @NotNull
    @Email
    @EqualsAndHashCode.Include
    @ToString.Include
    @Indexed
    private String email;

    @NotNull
    private String password;

    @Transient
    private String passwordConfirmation;

    @Reference
    @JsonIdentityReference( alwaysAsId = true )
    private Set<Role> roles = new HashSet<>();

    @Reference
    @JsonIdentityReference( alwaysAsId = true )
    private Set<Book> books = new HashSet<>();

    public void addRole( Role role ) {
        roles.add( role );
    }

    public void addBook( Book book ) {
        books.add( book );
    }

}
