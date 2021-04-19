package com.redislabs.edu.redi2read.controllers;

import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.models.Category;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CategoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Iterable<Book> allBooks() {
        return bookRepository.findAll();
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
