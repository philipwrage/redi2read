package com.redislabs.edu.redi2read.services;

import com.redislabs.redisgraph.Record;
import com.redislabs.redisgraph.ResultSet;
import com.redislabs.redisgraph.graph_entities.Node;
import com.redislabs.redisgraph.impl.api.RedisGraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class RecommendationService {

    @Value( "${app.graph-id}" )
    private String graphId;

    private final RedisGraph graph = new RedisGraph();

    public Set<String> getRecommendationsFromCommonPurchasesForUser( String userId ) {
        Set<String> recommendations = new HashSet<>();

        final String query = "MATCH (u:User {id: '%s' })-[:PURCHASED]->(ob:Book) " +
            "MATCH (ob)<-[:PURCHASED]-(:User)-[:PURCHASED]->(b:Book) " +
            "WHERE NOT (u)-[:PURCHASED]->(b) " +
            "RETURN distinct b, count(b) as frequency " +
            "ORDER BY frequency DESC";

        ResultSet resultSet = graph.query( graphId, String.format( query, userId ) );
        resultSet.forEach( record -> {
            recommendations.add( record.<Node>getValue( "b" ).getProperty( "id" ).getValue().toString() );
        } );
        return recommendations;
    }

    public Set<String> getFrequentlyBoughtTogether( String isbn ) {
        Set<String> recommendations = new HashSet<>();

        final String query = "MATCH (u:User)-[:PURCHASED]->(b1:Book {id: '%s' }) " +
            "MATCH (b1)<-[:PURCHASED]-(u)-[:PURCHASED]->(b2:Book) " +
            "MATCH rated = (User)-[:Rated]-(b2) " +
            "WITH b1, b2, count(b2) as freq, head(relationships(rated)) as r " +
            "WHERE b1 <> b2 " +
            "RETURN b2, freq, avg(r.rating) " +
            "ORDER BY freq, avg(r.rating) DESC";

        ResultSet resultSet = graph.query( graphId, String.format( query, isbn ) );
        resultSet.forEach( record -> {
            recommendations.add( record.<Node>getValue( "b2" ).getProperty( "id" ).getValue().toString() );
        } );
        return recommendations;
    }
}
