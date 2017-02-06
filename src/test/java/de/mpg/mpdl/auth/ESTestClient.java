package de.mpg.mpdl.auth;

import org.elasticsearch.client.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import de.mpg.mpdl.auth.configuration.AuthConfiguration;
import de.mpg.mpdl.auth.configuration.AuthTestConfiguration;
import de.mpg.mpdl.auth.service.es.ElasticSearchProviderService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AuthTestConfiguration.class)
@WebAppConfiguration
public class ESTestClient {
	
	@Autowired
	private ElasticSearchProviderService svc;
	
	@Test
	public void testService() {
		svc.getContextNames();
	}
	
	@Test
	public void testDates() {
		svc.getTestDoc();
		svc.addTestdoc();
	}
	
	
}
