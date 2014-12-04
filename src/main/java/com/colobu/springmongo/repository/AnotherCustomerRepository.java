package com.colobu.springmongo.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.colobu.springmongo.entity.Customer;

@Repository
public class AnotherCustomerRepository {
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public Customer save(Customer c) {
		mongoTemplate.save(c);
		return c;
	}
	
	public void deleteAll() {
		mongoTemplate.dropCollection(Customer.class);
	}
	
	public List<Customer> findAll() {
        return mongoTemplate.findAll(Customer.class);
    }
	
	public List<Customer> findByLastname(String lastname, Sort sort){
		Criteria criteria = new Criteria("lastname").is(lastname);
		return mongoTemplate.find(new Query(criteria), Customer.class);
	}

	public GeoResults<Customer> findByAddressLocationNear(Point point, Distance distance){
		return mongoTemplate.geoNear(NearQuery.near(point).maxDistance(distance), Customer.class);
	}
}