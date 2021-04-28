package com.redislabs.edu.redi2read.boot;

import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import com.redislabs.lettusearch.Suggestion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@Order( 7 )
@Slf4j
public class CreateAuthorNameSuggestions implements CommandLineRunner {

    private final RedisTemplate<String,String> redisTemplate;
    private final BookRepository bookRepository;
    private final StatefulRediSearchConnection<String,String> searchConnection;

    @Value( "${app.auto-complete-key}" )
    private String autoCompleteKey;

    public CreateAuthorNameSuggestions( RedisTemplate<String, String> redisTemplate,
                                        BookRepository bookRepository,
                                        StatefulRediSearchConnection<String, String> searchConnection ) {
        this.redisTemplate = redisTemplate;
        this.bookRepository = bookRepository;
        this.searchConnection = searchConnection;
    }

    @Override
    public void run( String... args ) throws Exception {
        Boolean hasAutoCompleteKey = Optional.ofNullable( redisTemplate.hasKey( autoCompleteKey ) ).orElse( false );
        if ( !hasAutoCompleteKey ) {
            RediSearchCommands<String,String> commands = searchConnection.sync();
            bookRepository.findAll()
                .forEach( book -> {
                    if ( book.getAuthors() != null ) {
                        book.getAuthors().forEach( author -> {
                            Suggestion<String> suggestion = Suggestion.builder( author ).score( 1d ).build();
                            commands.sugadd( autoCompleteKey, suggestion );
                        } );
                    }
                } );
            log.debug( "Created author name suggestions." );
        }
    }
}
