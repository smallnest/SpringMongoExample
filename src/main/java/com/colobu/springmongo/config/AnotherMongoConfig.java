package com.colobu.springmongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
@ComponentScan("com.colobu.springmongo")
public class AnotherMongoConfig extends AbstractMongoConfiguration{
	@Override
	protected String getDatabaseName() {
		return "test";
	}

	@Override
	@Bean
	public Mongo mongo() throws Exception {
		return new MongoClient("localhost:27017");
	}

	@Override
	protected String getMappingBasePackage() {
		return "com.colobu.springmongo.entity";
	}
}
