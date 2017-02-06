package de.mpg.mpdl.auth.service.es;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.get.GetField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.mpg.mpdl.auth.model.Grant;
import de.mpg.mpdl.auth.model.Testdoc;
import de.mpg.mpdl.auth.repository.GrantRepository;

@Service
public class ElasticSearchProviderService {
	
	@Autowired
	private Client client;
	
	@Autowired
	private GrantRepository repo;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Value("${context_index_name}")
	String ctx_index_name;
	
	@Value("${context_index_type}")
	String ctx_index_type;
	
	public void getContextNames() {
		List<Grant> grants = repo.findAll();
		System.out.println("got " + grants.size());
		grants.forEach(grant -> {
			if (grant.getTargetId().startsWith("vm44")) {
			System.out.println("handling grant " + grant.getId());
			String id = grant.getTargetId().substring(grant.getTargetId().lastIndexOf("/") +1);
			System.out.println("getting name of " + id);
			GetResponse response = client.prepareGet(ctx_index_name, ctx_index_type, id)
					.setFields("name").get();
			System.out.println(response.getFields().get("name").getValue());
			} else {
				System.out.println("grant " + grant.getId() + " has role " + grant.getRole().getName());
			}
		});
	}
	
	public void getTestDoc() {
		
		System.out.println(client.settings().get("cluster.name"));
		System.out.println(client.admin().indices().prepareExists("escidocdates").get().isExists());
		GetResponse response = client.prepareGet("escidocdates", "testdoc", "AVjfRQ1Hw6hmI-oC6ogs").get();
		System.out.println("response: " + response.getSourceAsString());
		try {
			Testdoc doc = mapper.readValue(response.getSourceAsBytes(), Testdoc.class);
			System.out.println("getting date: " + doc.getDate());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addTestdoc() {
		Testdoc doc = new Testdoc();
		doc.setDate("2013-11");
		
		byte[] source = null;
		try {
			source = mapper.writeValueAsBytes(doc);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("The source: " + source.toString());
		IndexResponse response = client.prepareIndex("escidocdates", "testdoc").setSource(source).get();
		System.out.println(response.getId());
	}

}
