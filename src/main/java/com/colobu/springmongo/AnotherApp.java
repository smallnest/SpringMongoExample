package com.colobu.springmongo;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeospatialIndex;

import com.colobu.springmongo.config.AnotherMongoConfig;
import com.colobu.springmongo.entity.Address;
import com.colobu.springmongo.entity.Customer;
import com.colobu.springmongo.repository.AnotherCustomerRepository;


public class AnotherApp {
	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(AnotherMongoConfig.class);
		MongoTemplate mongoTemplate = context.getBean(MongoTemplate.class);
		AnotherCustomerRepository repository = context.getBean(AnotherCustomerRepository.class);
		
		repository.deleteAll();
		Customer dave, oliver, carter;
		dave = repository.save(new Customer("Dave", "Matthews"));
		oliver = repository.save(new Customer("Oliver August", "Matthews"));
		carter = repository.save(new Customer("Carter", "Beauford"));
		
		List<Customer> result = repository.findByLastname("Matthews", new Sort(Direction.ASC, "firstname"));
		System.out.println(result.size());
		
		GeospatialIndex indexDefinition = new GeospatialIndex("address.location");
		indexDefinition.getIndexOptions().put("min", -180);
		indexDefinition.getIndexOptions().put("max", 180);
		mongoTemplate.indexOps(Customer.class).ensureIndex(indexDefinition);
		
		Customer ollie = new Customer("Oliver", "Gierke");
		ollie.setAddress(new Address(new Point(52.52548, 13.41477)));
		ollie = repository.save(ollie);
		Point referenceLocation = new Point(52.51790, 13.41239);
		Distance oneKilometer = new Distance(1, Metrics.KILOMETERS);
		GeoResults<Customer> geoResult = repository.findByAddressLocationNear(referenceLocation, oneKilometer);
		System.out.println(geoResult.getContent().size());
	}
}
