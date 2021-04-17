package com.redislabs.edu.redi2read.controllers;

import com.redislabs.edu.redi2read.models.User;
import com.redislabs.edu.redi2read.repositories.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping( "/api/users" )
public class UserController {

    private final UserRepository userRepository;

    public UserController( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Iterable<User> allUsers( @RequestParam( defaultValue = "" ) String email ) {
        if ( email.isEmpty() ) {
            return userRepository.findAll();
        } else {
            Optional<User> user = Optional.ofNullable( userRepository.findFirstByEmail( email ) );
            return user.map( List::of ).orElse( Collections.emptyList() );
        }
    }

}
