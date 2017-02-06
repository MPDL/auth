package de.mpg.mpdl.auth.service.es;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ElasticTransportClient {

	private static final String MAPPING_JSON_PATH = "/home/frank/data/git/auth/src/main/resources/json/";


	public static TransportClient start() {
		Settings settings = Settings.builder().put("cluster.name", "elastic_inge").put("client.transport.sniff", true).build();
		TransportClient client = new TransportClient.Builder().settings(settings).build();

		try {
			// client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.20.2.95"), 8888));
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("134.76.28.200"), 8080));
			System.out.println("Settings:");
		      client.settings().getAsMap().forEach((k,v) -> System.out.println(k + "   " + v));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return client;
	}

	public static void createIndex(String indexName) {
		Client c = start();

		IndicesExistsResponse res = c.admin().indices().prepareExists(indexName).execute().actionGet();
		if (res.isExists()) {
			DeleteIndexRequestBuilder delIdx = c.admin().indices().prepareDelete(indexName);
			delIdx.execute().actionGet();
		}

		CreateIndexRequestBuilder indexReq = c.admin().indices().prepareCreate(indexName);
		CreateIndexResponse indexResp = indexReq.execute().actionGet();

		System.out.println("created index " + indexName + ": " + indexResp.isAcknowledged());
		c.close();

	}
	
	public static void deleteIndex(String index) {
		Client c = start();
		DeleteIndexResponse delResponse = c.admin().indices()
				.prepareDelete(index).execute().actionGet();
		if (delResponse.isAcknowledged()) {
			System.out.println("Deleted index: " + index);
		}
		c.close();
	}
	
public static void saveExistingMappingToFile(String indexname, String type, String outputfilename) {
		
		Client c = start();
		ObjectMapper om = new ObjectMapper();
		JsonFactory jf = new JsonFactory();

		GetMappingsResponse mappingResponse = c.admin().indices().prepareGetMappings().execute().actionGet();
		try {
			JsonParser jp = jf.createParser(mappingResponse.getMappings().get(indexname).get(type).get().source().uncompressed());
			JsonNode jn = om.readTree(jp);
			om.writerWithDefaultPrettyPrinter().writeValue(new File(MAPPING_JSON_PATH + outputfilename), jn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			c.close();
		}
	}
	
	public static void addMapping(String index, String type, String jsonFile) {
		java.nio.file.Path path = Paths.get(MAPPING_JSON_PATH + jsonFile);
		byte[] mapping = null;
		try {
			mapping = Files.readAllBytes(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Client c = start();
		PutMappingRequestBuilder mappingReq = c.admin().indices()
				.preparePutMapping(index);
		mappingReq.setType(type);
		mappingReq.setSource(new String(mapping, StandardCharsets.UTF_8));

		PutMappingResponse mappingResp = mappingReq.execute().actionGet();
		System.out.println("added mapping to index" + index + ": "
				+ mappingResp.isAcknowledged());
		c.close();

	}
	
	public static void addAlias(String index, String alias) {
		Client c = start();
		IndicesAliasesResponse resp = c.admin().indices().prepareAliases()
				.addAlias(index, alias).execute().actionGet();
		if (resp.isAcknowledged()) {
				System.out.println(resp.isAcknowledged());
		}
		c.close();
	}
	
	public static void removeAlias(String index, String alias) {
		Client c = start();
		IndicesAliasesResponse resp = c.admin().indices().prepareAliases()
				.removeAlias(index, alias).execute().actionGet();
		if (resp.isAcknowledged()) {
				System.out.println(resp.isAcknowledged());
		}
		c.close();
	}


	public static void main(String... strings) {

		//saveExistingMappingToFile("pureitems", "pubitem", "pubitem_mapping.json");
		deleteIndex("pure_20170120");
		createIndex("pure_20170120");
		addMapping("pure_20170120", "pubitem", "pubitem_mapping.json");
	}
}
