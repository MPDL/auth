package de.mpg.mpdl.auth.service.es;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.util.EntityUtils;
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
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryShardContext;
import org.elasticsearch.script.Script;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.hibernate.validator.internal.metadata.aggregated.rule.ReturnValueMayOnlyBeMarkedOnceAsCascadedPerHierarchyLine;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ElasticTransportClient {

	private static final String MAPPING_JSON_PATH = "/home/frank/data/git/auth/src/main/resources/json/";


	public static TransportClient start() {
		Settings settings = Settings.builder().put("cluster.name", "elastic_inge").put("client.transport.sniff", true).build();
		TransportClient client = new PreBuiltTransportClient(settings);

		try {
			//client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.20.2.11"), 7777));
			 client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("134.76.28.200"), 8080));
			// System.out.println("Settings:");
		    // client.settings().getAsMap().forEach((k,v) -> System.out.println(k + "   " + v));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return client;
	}
	
	public static RestClient rest() {
		RestClient client = RestClient.builder(new HttpHost("b253.test")).build();
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
			JsonParser jp = jf.createParser(mappingResponse.getMappings().get(indexname).get(type).source().uncompressed());
			JsonNode jn = om.readTree(jp);
			Map<String, Object> map =mappingResponse.mappings().get(indexname).get(type).sourceAsMap();
			om.writerWithDefaultPrettyPrinter().writeValue(new File(MAPPING_JSON_PATH + outputfilename), jn);
			//om.writerWithDefaultPrettyPrinter().writeValue(System.out, map);

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
	
	public static JsonNode reindexBody(String src, String dest) {
		
		JsonNode body = JsonNodeFactory.instance.objectNode();
		ObjectNode source = ((ObjectNode) body).putObject("source");
		source.put("index", src);
		ObjectNode target = ((ObjectNode) body).putObject("dest");
		target.put("index", dest);
		return body;
	}
	
	public static void restDemo(HttpEntity entity) {
		RestClient restClient = rest();
		ObjectMapper mapper = new ObjectMapper();
		Response response;
		try {
			response = restClient.performRequest("GET", "/inge/pure_20170203/_search",
			        Collections.singletonMap("pretty", "true"), entity);
			//System.out.println(EntityUtils.toString(response.getEntity()));
			JsonNode searchHits = mapper.readTree(response.getEntity().getContent());
			searchHits.fieldNames().forEachRemaining(s -> System.out.println(s));
			searchHits.path("hits").fieldNames().forEachRemaining(s -> System.out.println(s));
			for (JsonNode node : searchHits.path("hits").path("hits")) {
				System.out.println(node.path("_id").asText());
				// node.path("_source").fieldNames().forEachRemaining(s -> System.out.println(s));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				restClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void reindexViaRest() {
		long start = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		RestClient client = rest();
		byte[] stream = null;;
		try {
			stream = mapper.writeValueAsBytes(reindexBody("rest_20170207", "rest_20170209"));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpEntity entity = EntityBuilder.create().setBinary(stream).build();
		try {
			Response response = client.performRequest("POST", "/inge/_reindex", Collections.singletonMap("pretty", "true"), entity);
			long end = System.currentTimeMillis() - start;
			System.out.println(response.getStatusLine().getStatusCode());
			System.out.println("reindexing via rest takes: " + end);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		client.performRequestAsync("POST", "/inge/_reindex", Collections.singletonMap("pretty", "true"), entity, new ResponseListener() {
			
			@Override
			public void onSuccess(Response arg0) {
				long end = System.currentTimeMillis() - start;
				System.out.println(arg0.getStatusLine().getStatusCode());
				System.out.println("reindexing via rest takes: " + end);
			}
			
			@Override
			public void onFailure(Exception arg0) {
				arg0.printStackTrace();
			}
		});"query"
		*/
	}
	
	public static QueryBuilder prepareQuery() {
		QueryBuilder builder = QueryBuilders
				.boolQuery()
				.must(QueryBuilders.matchQuery("metadata.title", "laser"))
				.should(QueryBuilders.matchQuery("version.state", "RELEASED"))
				.should(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("context.objectId", "escidoc:1855158"))
						.should(QueryBuilders.matchQuery("publicStatus", "SUBMITTED"))
						.should(QueryBuilders.matchQuery("publicStatus", "RELEASED"))
						.should(QueryBuilders.matchQuery("publicStatus", "IN_REVISION"))
						.should(QueryBuilders.matchQuery("publicStatus", "WITHDRAWN"))
						.minimumShouldMatch(1))
				.should(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("context.objectId", "escidoc:1855158"))
						.must(QueryBuilders.matchQuery("owner.objectId", "escidoc:1855305"))
						.should(QueryBuilders.matchQuery("version.state", "SUBMITTED"))
						.should(QueryBuilders.matchQuery("version.state", "RELEASED"))
						.should(QueryBuilders.matchQuery("version.state", "IN_REVISION"))
						.should(QueryBuilders.matchQuery("version.state", "PENDING"))
						.minimumShouldMatch(1))
				.minimumShouldMatch(3);
				
		return builder;

	}
	
	public static String getQueryString(QueryBuilder qb) {
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode qbNode = mapper.readTree(qb.toString());
			JsonNode newNode = mapper.createObjectNode();
			JsonNode rootNode = ((ObjectNode) newNode).putObject("query");
			JsonNode queryNode = ((ObjectNode) rootNode).set("query", qbNode);
			String query = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(queryNode);
			return query;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public static void main(String... strings) {
		
		// String query = "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"metadata.genre\":\"ARTICLE\"}}],\"filter\":[{\"script\":{\"script\":{\"inline\":\"doc['latestVersion.versionNumber'].value == doc['version.versionNumber'].value\",\"lang\":\"painless\"},\"boost\":1.0}}],\"disable_coord\":false,\"adjust_pure_negative\":true,\"boost\":1.0,\"_name\":\"query\"}}}";
		/*
		QueryBuilder qb = prepareQuery();
		
		String query = getQueryString(qb);
		System.out.println(query);
		HttpEntity e = EntityBuilder.create().setText(query).build();
		restDemo(e);
		*/
		//saveExistingMappingToFile("rest_20170207", "item", "item_mapping.json");
		//deleteIndex("rest_20170207");
		//createIndex("pure_store");
		//ElasticClusterInfoService.indexInfo("pure_20170203", "item");
		//addMapping("pure_store", "item", "item_mapping.json");
		//ElasticClusterInfoService.listIndices().forEach(s -> System.out.println(s));
		ElasticSearchService.scroll(start(), "pure_store");
		// reindexViaRest();
	}
}
