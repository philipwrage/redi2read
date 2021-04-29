package com.redislabs.edu.redi2read.services;

import com.redislabs.redisgraph.impl.api.RedisGraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    @Value( "${app.graph-id}" )
    private String graphId;

    private RedisGraph graph = new RedisGraph();
    
}
