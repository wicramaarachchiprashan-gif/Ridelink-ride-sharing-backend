package com.ridelink.farepaymentservice.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
    basePackages = "com.ridelink.farepaymentservice.repository",
    mongoTemplateRef = "fareMongoTemplate"
)
public class MongoConfig {

    @Bean(name = "fareMongoClient")
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017/ridelink_fare_payment_db");
    }

    @Bean(name = "fareMongoTemplate")
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "ridelink_fare_payment_db");
    }
}