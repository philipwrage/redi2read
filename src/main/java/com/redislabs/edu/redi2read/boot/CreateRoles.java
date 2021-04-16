package com.redislabs.edu.redi2read.boot;

import com.redislabs.edu.redi2read.models.Role;
import com.redislabs.edu.redi2read.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order( 1 )
@Slf4j
public class CreateRoles implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public CreateRoles( RoleRepository roleRepository ) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run( String... args ) throws Exception {
        if ( roleRepository.count() == 0 ) {
            Role adminRole = Role.builder().name( "admin" ).build();
            Role customerRole = Role.builder().name( "customer" ).build();
            roleRepository.save( adminRole );
            roleRepository.save( customerRole );
            log.debug( "Created admin and customer roles." );
        }
    }
}
