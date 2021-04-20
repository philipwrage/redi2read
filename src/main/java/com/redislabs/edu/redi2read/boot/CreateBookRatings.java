package com.redislabs.edu.redi2read.boot;

import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.models.BookRating;
import com.redislabs.edu.redi2read.models.User;
import com.redislabs.edu.redi2read.repositories.BookRatingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@Order( 4 )
@Slf4j
public class CreateBookRatings implements CommandLineRunner {

    private final BookRatingRepository bookRatingRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final BookRatingProperties bookRatingProperties;

    public CreateBookRatings( BookRatingRepository bookRatingRepository,
                              RedisTemplate<String, String> redisTemplate,
                              BookRatingProperties bookRatingProperties ) {
        this.bookRatingRepository = bookRatingRepository;
        this.redisTemplate = redisTemplate;
        this.bookRatingProperties = bookRatingProperties;
    }

    @Override
    public void run( String... args ) throws Exception {
        if ( bookRatingRepository.count() == 0 ) {
            Random random = new Random();

            IntStream.range( 0, bookRatingProperties.getNumberOfRatings() )
                .forEach( value -> {
                    int stars = random.nextInt( bookRatingProperties.getRatingStars() ) + 1;

                    String bookId = redisTemplate.opsForSet()
                        .randomMember( Book.class.getName() );
                    Book book = new Book();
                    book.setId( bookId );

                    String userId = redisTemplate.opsForSet()
                        .randomMember( User.class.getName() );
                    User user = new User();
                    user.setId( userId );

                    BookRating bookRating = BookRating.builder()
                        .user( user )
                        .book( book )
                        .rating( stars )
                        .build();

                    bookRatingRepository.save( bookRating );
                } );

            log.debug( "Created {} random book ratings.", bookRatingProperties.getNumberOfRatings() );
        }
    }

}
