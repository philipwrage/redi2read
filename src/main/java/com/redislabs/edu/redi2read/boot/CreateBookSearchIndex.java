package com.redislabs.edu.redi2read.boot;

import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.lettusearch.CreateOptions;
import com.redislabs.lettusearch.Field;
import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import io.lettuce.core.RedisCommandExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order( 6 )
@Slf4j
public class CreateBookSearchIndex implements CommandLineRunner {

    private final StatefulRediSearchConnection<String, String> searchConnection;

    @Value( "${app.book-search-index-name}" )
    private String bookSearchIndexName;

    public CreateBookSearchIndex( StatefulRediSearchConnection<String, String> searchConnection ) {
        this.searchConnection = searchConnection;
    }

    @Override
    public void run( String... args ) throws Exception {
        log.debug( "Starting CommandLineRunner: {}", getClass().getName() );
        RediSearchCommands<String, String> searchCommands = searchConnection.sync();

        try {
            searchCommands.ftInfo( bookSearchIndexName );
        } catch ( RedisCommandExecutionException e ) {

            if ( e.getMessage().equalsIgnoreCase( "Unknown Index Name" ) ) {
                CreateOptions<String, String> options = CreateOptions
                    .<String, String>builder()
                    .prefix( String.format( "%s:", Book.class.getName() ) )
                    .build();

                Field<String> title = Field.text( "title" ).sortable( true ).build();
                Field<String> subTitle = Field.text( "subtitle" ).build();
                Field<String> description = Field.text( "description" ).build();
                Field<String> author0 = Field.text( "authors.[0]" ).build();
                Field<String> author1 = Field.text( "authors.[1]" ).build();
                Field<String> author2 = Field.text( "authors.[2]" ).build();
                Field<String> author3 = Field.text( "authors.[3]" ).build();
                Field<String> author4 = Field.text( "authors.[4]" ).build();
                Field<String> author5 = Field.text( "authors.[5]" ).build();
                Field<String> author6 = Field.text( "authors.[6]" ).build();

                searchCommands.create( bookSearchIndexName,
                    options,
                    title,
                    subTitle,
                    description,
                    author0,
                    author1,
                    author2,
                    author3,
                    author4,
                    author5,
                    author6 );

                log.debug( "Created book search index." );
            }
        }
    }
}
