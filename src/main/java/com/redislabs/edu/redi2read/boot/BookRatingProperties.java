package com.redislabs.edu.redi2read.boot;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@ConstructorBinding
@ConfigurationProperties( prefix = "app.book-ratings" )
@Validated
public class BookRatingProperties {

    @Min( 0 )
    @Max( 10000 )
    private final Integer numberOfRatings;

    @Min( 0 )
    @Max( 10 )
    private final Integer ratingStars;

    public BookRatingProperties( @DefaultValue( "5000" ) Integer numberOfRatings,
                                 @DefaultValue( "5" ) Integer ratingStars ) {

        this.numberOfRatings = numberOfRatings;
        this.ratingStars = ratingStars;
    }

}
