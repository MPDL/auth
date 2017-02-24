package de.mpg.mpdl.auth.configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.mpg.mpdl.auth.service.CurrentDateTimeService;
import de.mpg.mpdl.auth.service.DateTimeService;

@Configuration
@ComponentScan(basePackages = {"de.mpg.mpdl.auth"})
@PropertySources({
	@PropertySource("classpath:jpa.properties"),
	@PropertySource(value = "file:${catalina.home.dir}/conf/auth.properties", ignoreResourceNotFound = true)
})
@Import(value = {JPAConfiguration.class})
public class AuthTestConfiguration {
	
	@Bean
    DateTimeService currentDateTimeService() {
        return new CurrentDateTimeService();
    }
	
	@Bean
    MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    static
    PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Value("${es_cluster_name}")
    String clusterName;

    @Value("${es_transport_ips}")
    String transportIps;

    @Bean
    public Client client() {

      Settings settings =
          Settings.builder().put("cluster.name", clusterName)
              //.put("client.transport.sniff", true)
              .build();
      TransportClient client = new PreBuiltTransportClient(settings);
      for (String ip : transportIps.split(" ")) {
        String addr = ip.split(":")[0];
        int port = Integer.valueOf(ip.split(":")[1]);
        try {
          client
              .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(addr), port));
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
      }
      return client;
    }
    
    @Bean
    public ObjectMapper mapper() {
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      return mapper;
    }
}
