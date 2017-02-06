package de.mpg.mpdl.auth.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@PropertySources({
	@PropertySource("classpath:test_jpa.properties"),
	@PropertySource(value = "file:/opt/apache-tomcat-7.0.63/conf/auth.properties", ignoreResourceNotFound = true)
})
@ComponentScan(basePackages = {"de.mpg.mpdl.auth"})
public class TestConfiguration extends WebMvcConfigurerAdapter{
	
	@Bean
	  public SpelAwareProxyProjectionFactory projectionFactory() {
	    return new SpelAwareProxyProjectionFactory();
	  }

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
    static
    PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
