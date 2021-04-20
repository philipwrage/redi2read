package com.redislabs.edu.redi2read.repositories;

import com.redislabs.edu.redi2read.models.Cart;
import com.redislabs.modules.rejson.JReJSON;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class CartRepository implements CrudRepository<Cart, String> {

    private static final String idPrefix = Cart.class.getName();
    public static final String CARTS_BY_USER_ID_IDX = "carts-by-user-id-idx";
    private final RedisTemplate<String, String> redisTemplate;
    private JReJSON redisJson = new JReJSON();

    public CartRepository( RedisTemplate<String, String> redisTemplate ) {
        this.redisTemplate = redisTemplate;
    }

    private SetOperations<String, String> redisSets() {
        return redisTemplate.opsForSet();
    }

    private HashOperations<String, String, String> redisHash() {
        return redisTemplate.opsForHash();
    }

    @Override
    public <S extends Cart> S save( S cart ) {
        if ( cart.getId() == null ) {
            cart.setId( UUID.randomUUID().toString() );
        }
        String key = getKey( cart );
        redisJson.set( key, cart );
        redisSets().add( idPrefix, key );
        redisHash().put( CARTS_BY_USER_ID_IDX, cart.getUserId(), cart.getId() );
        return cart;
    }

    @Override
    public <S extends Cart> Iterable<S> saveAll( Iterable<S> carts ) {
        return StreamSupport
            .stream( carts.spliterator(), false )
            .map( this::save )
            .collect( Collectors.toList());
    }

    @Override
    public Optional<Cart> findById( String id ) {
        return Optional.ofNullable( redisJson.get( getKey( id ), Cart.class ) );
    }

    @Override
    public boolean existsById( String id ) {
        return Optional.ofNullable( redisTemplate.hasKey( getKey( id ) ) )
            .orElse( false );
    }

    @Override
    public Iterable<Cart> findAll() {
        String[] keys = Objects.requireNonNull( redisSets().members( idPrefix ) )
            .toArray( String[]::new );
        return redisJson.mget( Cart.class, keys );
    }

    @Override
    public Iterable<Cart> findAllById( Iterable<String> ids ) {
        String[] keys = StreamSupport
            .stream( ids.spliterator(), false )
            .map( CartRepository::getKey )
            .toArray( String[]::new );
        return redisJson.mget( Cart.class, keys );
    }

    @Override
    public long count() {
        return Optional.ofNullable( redisSets().size( idPrefix ) ).orElse( 0L );
    }

    @Override
    public void deleteById( String id ) {
        redisJson.del( getKey( id ) );
    }

    @Override
    public void delete( Cart cart ) {
        deleteById( cart.getId() );
    }

    @Override
    public void deleteAll( Iterable<? extends Cart> carts ) {
        List<String> keys = StreamSupport
            .stream( carts.spliterator(), false )
            .map( CartRepository::getKey )
            .collect( Collectors.toList() );
        redisSets().getOperations().delete( keys );
    }

    @Override
    public void deleteAll() {
        redisSets().getOperations()
            .delete( Objects.requireNonNull( redisSets().members( idPrefix ) ) );
    }

    public Optional<Cart> findByUserId( Long userId ) {
        String cartId = redisHash().get( CARTS_BY_USER_ID_IDX, userId.toString() );
        return (cartId != null) ? findById( cartId ) : Optional.empty();
    }

    public static <S extends Cart> String getKey( S cart ) {
        return getKey( cart.getId() );
    }

    public static String getKey( String id ) {
        return String.format( "%s:%s", idPrefix, id );
    }

}
