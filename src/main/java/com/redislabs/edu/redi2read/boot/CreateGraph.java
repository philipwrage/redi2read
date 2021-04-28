package com.redislabs.edu.redi2read.boot;

import com.redislabs.edu.redi2read.repositories.BookRatingRepository;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CategoryRepository;
import com.redislabs.edu.redi2read.repositories.UserRepository;
import com.redislabs.redisgraph.impl.api.RedisGraph;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@Order( 8 )
@Slf4j
public class CreateGraph implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookRatingRepository bookRatingRepository;
    private final CategoryRepository categoryRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value( "${app.graph-id}" )
    private String graphId;

    public CreateGraph( UserRepository userRepository,
                        BookRepository bookRepository,
                        BookRatingRepository bookRatingRepository,
                        CategoryRepository categoryRepository,
                        RedisTemplate<String, String> redisTemplate ) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.categoryRepository = categoryRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run( String... args ) throws Exception {
        log.debug( "Starting CommandLineRunner: {}", getClass().getName() );
        final String CREATE_CATEGORY_NODE = "CREATE (:Category {id: \"%s\", name: \"%s\"})";
        final String CREATE_BOOK_NODE = "CREATE (:Book {id: \"%s\", title: \"%s\"})";
        final String CREATE_AUTHOR_NODE = "CREATE (:Author {name: \"%s\"})";
        final String CREATE_USER_NODE = "CREATE (:User {id: \"%s\", name: \"%s\"})";
        final String CREATE_AUTHORED_EDGE = "MATCH (a:Author {name: \"%s\"}), (b:Book {id: \"%s\"}) CREATE (a)-[:AUTHORED]->(b)";
        final String CREATE_IN_EDGE = "MATCH (b:Book {id: \"%s\"}), (c:Category {id: \"%s\"}) CREATE (b)-[:IN]->(c)";
        final String CREATE_PURCHASED_EDGE = "MATCH (u:User {id: \"%s\"}), (b:Book {id: \"%s\"}) CREATE (u)-[:PURCHASED]->(b)";
        final String CREATE_RATED_EDGE = "MATCH (u:User {id: \"%s\"}), (b:Book {id: \"%s\"}) CREATE (u)-[:RATED {rating: %s}]->(b)";

        Boolean hasGraphKey = Optional.ofNullable( redisTemplate.hasKey( graphId ) ).orElse( false );
        if ( !hasGraphKey ) {
            try ( RedisGraph graph = new RedisGraph() ) {
                Arrays.asList( ":Book(id)", ":User(id)", ":Category(id)", ":Author(name)" )
                    .forEach( s -> graph.query( graphId, "CREATE INDEX ON " + s ) );

                categoryRepository.findAll().forEach( category ->
                    graph.query( graphId, String.format( CREATE_CATEGORY_NODE, category.getId(), category.getName() ) ) );

                Set<String> authors = new HashSet<>();
                bookRepository.findAll().forEach( book -> {
                    graph.query( graphId, String.format( CREATE_BOOK_NODE, book.getId(), book.getTitle() ) );
                    if ( book.getAuthors() != null ) {
                        book.getAuthors().forEach( author -> {
                            if ( !authors.contains( author ) ) {
                                authors.add( author );
                                graph.query( graphId, String.format( CREATE_AUTHOR_NODE, author ) );
                            }
                            graph.query( graphId, String.format( CREATE_AUTHORED_EDGE, author, book.getId() ) );
                        } );
                        book.getCategories().forEach( category ->
                            graph.query( graphId, String.format( CREATE_IN_EDGE, book.getId(), category.getId() ) ) );
                    }
                } );

                userRepository.findAll().forEach( user -> {
                    graph.query( graphId, String.format( CREATE_USER_NODE, user.getId(), user.getName() ) );
                    user.getBooks().forEach( book ->
                        graph.query( graphId, String.format( CREATE_PURCHASED_EDGE, user.getId(), book.getId() ) ) );
                } );

                bookRatingRepository.findAll().forEach( bookRating ->
                    graph.query( graphId, String.format( CREATE_RATED_EDGE, bookRating.getUser().getId(),
                        bookRating.getBook().getId(), bookRating.getRating() ) ) );
            }
            log.debug( "Created graph." );
        }
    }
}
