package com.redislabs.edu.redi2read.controllers;

import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.models.Category;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping( "/api/books" )
public class BookController {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookController( BookRepository bookRepository,
                           CategoryRepository categoryRepository ) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> allBooks( @RequestParam( defaultValue = "0" ) Integer page,
                                                         @RequestParam( defaultValue = "10" ) Integer size ) {
        Pageable pageable = PageRequest.of( page, size );
        Page<Book> pagedResult = bookRepository.findAll( pageable );
        List<Book> books = pagedResult.hasContent() ? pagedResult.getContent() : Collections.emptyList();
        Map<String, Object> response = new HashMap<>();
        response.put( "books", books );
        response.put( "page", pagedResult.getNumber() );
        response.put( "pages", pagedResult.getTotalPages() );
        response.put( "total", pagedResult.getTotalElements() );
        return new ResponseEntity<>( response, new HttpHeaders(), HttpStatus.OK );
    }

    @GetMapping( "/categories" )
    public Iterable<Category> allCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping( "/{isbn}" )
    public Book getBook( @PathVariable( "isbn" ) String isbn ) {
        return bookRepository.findById( isbn ).get();
    }
}
