package colobu.com.springmongo;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.colobu.springmongo.config.AnotherMongoConfig;
import com.colobu.springmongo.entity.Address;
import com.colobu.springmongo.entity.Customer;
import com.colobu.springmongo.repository.AnotherCustomerRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AnotherMongoConfig.class })
public class AnotherAppTest {

	@Autowired
	AnotherCustomerRepository repository;
	@Autowired
	MongoOperations operations;
	Customer dave, oliver, carter;

	@Before
	public void setUp() {
		repository.deleteAll();
		dave = repository.save(new Customer("Dave", "Matthews"));
		oliver = repository.save(new Customer("Oliver August", "Matthews"));
		carter = repository.save(new Customer("Carter", "Beauford"));
	}

	/**
	 * Test case to show that automatically generated ids are assigned to the
	 * domain objects.
	 */
	@Test
	public void setsIdOnSave() {
		Customer dave = repository.save(new Customer("Dave", "Matthews"));
		assertNotNull(dave.getId());
	}

	@Test
	public void findCustomersUsingQuerydslSort() {
		List<Customer> result = repository.findByLastname("Matthews", new Sort(Direction.ASC, "firstname"));
		assertThat(result, hasSize(2));
		assertThat(result.get(1), is(oliver));
		assertThat(result.get(0), is(dave));
		
	}

	/**
	 * Test case to show the usage of the geo-spatial APIs to lookup people
	 * within a given distance of a reference point.
	 */
	@Test
	public void exposesGeoSpatialFunctionality() {
		GeospatialIndex indexDefinition = new GeospatialIndex("address.location");
		indexDefinition.getIndexOptions().put("min", -180);
		indexDefinition.getIndexOptions().put("max", 180);
		operations.indexOps(Customer.class).ensureIndex(indexDefinition);
		Customer ollie = new Customer("Oliver", "Gierke");
		ollie.setAddress(new Address(new Point(52.52548, 13.41477)));
		ollie = repository.save(ollie);
		Point referenceLocation = new Point(52.51790, 13.41239);
		Distance oneKilometer = new Distance(1, Metrics.KILOMETERS);
		GeoResults<Customer> result = repository.findByAddressLocationNear(referenceLocation, oneKilometer);
		assertThat(result.getContent(), hasSize(1));
		Distance distanceToFirstStore = result.getContent().get(0).getDistance();
		assertThat(distanceToFirstStore.getMetric(), is((Metric)Metrics.KILOMETERS));
		assertThat(distanceToFirstStore.getValue(), closeTo(0.862, 0.001));
	}

}
