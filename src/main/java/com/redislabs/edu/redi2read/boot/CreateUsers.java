package com.redislabs.edu.redi2read.boot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redislabs.edu.redi2read.models.Role;
import com.redislabs.edu.redi2read.models.User;
import com.redislabs.edu.redi2read.repositories.RoleRepository;
import com.redislabs.edu.redi2read.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@Order( 2 )
@Slf4j
public class CreateUsers implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CreateUsers( RoleRepository roleRepository,
                        UserRepository userRepository,
                        BCryptPasswordEncoder bCryptPasswordEncoder ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void run( String... args ) throws Exception {
        if ( userRepository.count() == 0 ) {
            Role adminRole = roleRepository.findFirstByName( "admin" );
            Role customerRole = roleRepository.findFirstByName( "customer" );

            try {
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<List<User>> userTypeReference = new TypeReference<>() {
                };
                InputStream inputStream = getClass().getResourceAsStream( "/data/users/users.json" );
                List<User> users = mapper.readValue( inputStream, userTypeReference );

                users.forEach( user -> {
                    user.setPassword( bCryptPasswordEncoder.encode( user.getPassword() ) );
                    user.addRole( customerRole );
                    userRepository.save( user );
                } );

                log.debug( "Imported {} users.", users.size() );
            } catch ( IOException e ) {
                log.error( "Unable to import users.", e );
            }

            User adminUser = new User();
            adminUser.setName( "Administrator" );
            adminUser.setEmail( "admin@example.com" );
            adminUser.setPassword( bCryptPasswordEncoder.encode( "secret" ) );
            adminUser.addRole( adminRole );
            userRepository.save( adminUser );

            log.debug( "Loaded user data, created admin user, and saved all to Redis." );
        }
    }
}
