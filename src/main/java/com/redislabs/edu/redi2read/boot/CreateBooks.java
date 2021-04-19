package com.redislabs.edu.redi2read.boot;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.models.Category;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Order( 3 )
@Slf4j
public class CreateBooks implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public CreateBooks( BookRepository bookRepository,
                        CategoryRepository categoryRepository ) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run( String... args ) throws Exception {
        if ( bookRepository.count() == 0 ) {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<Book>> bookTypeReference = new TypeReference<>() {
            };

            List<File> files =
                Files.list( Paths.get( Objects.requireNonNull( getClass().getResource( "/data/books" ) ).toURI() ) )
                    .filter( Files::isRegularFile )
                    .filter( path -> path.toString().endsWith( ".json" ) )
                    .map( Path::toFile )
                    .collect( Collectors.toList() );

            Map<String, Category> categories = new HashMap<>();

            files.forEach( file -> {
                try {
                    log.debug( "Processing book: {}", file.getPath() );
                    String categoryName = file.getName().substring( 0, file.getName().lastIndexOf( '_' ) );
                    log.debug( "Category: {}", categoryName );

                    Category category;
                    if ( !categories.containsKey( categoryName ) ) {
                        category = Category.builder().name( categoryName ).build();
                        categoryRepository.save( category );
                        categories.put( categoryName, category );
                    } else {
                        category = categories.get( categoryName );
                    }

                    InputStream inputStream = new FileInputStream( file );
                    List<Book> books = objectMapper.readValue( inputStream, bookTypeReference );
                    books.forEach( book -> {
                        book.addCategory( category );
                        bookRepository.save( book );
                    } );
                    log.debug( "Saved {} books.", books.size() );

                } catch ( FileNotFoundException e ) {
                    log.error( "Unable to create FileInputStream for file: {}", file.getPath() );
                } catch ( IOException e ) {
                    log.error( "Unable to parse JSON from within file: {}", file.getPath() );
                }
            } );

            log.debug( "Loaded book data and created books." );
        }
    }
}
