package de.mpg.mpdl.auth.service.es;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ElasticSearchService {

	public static void reindexl(TransportClient client, String srcIndex, String destIndex) {

		ObjectMapper om = new ObjectMapper();
		JsonFactory jf = new JsonFactory();

		BulkProcessor processor = BulkProcessor.builder(client, new BulkProcessor.Listener() {

			@Override
			public void beforeBulk(long arg0, BulkRequest arg1) {
				System.out.println(arg1.numberOfActions());
			}

			@Override
			public void afterBulk(long arg0, BulkRequest arg1, Throwable arg2) {
				System.out.println(arg2.getMessage());
			}

			@Override
			public void afterBulk(long arg0, BulkRequest arg1, BulkResponse arg2) {
				System.out.println(arg2.getTook().format());
			}
		})
				// default value 4 actions is 1000
				.setBulkActions(1000)
				// default value 4 size is 5mb
				.setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
				// default value 4 flush interval is not set
				.setFlushInterval(TimeValue.timeValueSeconds(5))
				// default value 4 convurrent requests is 1, which means an
				// asynchronous execution of the flush operation
				.setConcurrentRequests(1)
				// default values 4 backoffPolicy: retries: 8, start delay 50ms
				.setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)).build();

		ArrayList<String> hitIdList = new ArrayList<>();
		ArrayList<String> indexList = new ArrayList<>();

		int chunkSize = 1000;
		System.out.println("Starting scroll with chunks of " + chunkSize);
		long start = System.currentTimeMillis();

		QueryBuilder qb = QueryBuilders.matchAllQuery();

		SearchResponse response = client.prepareSearch(srcIndex)
				// .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
				.setScroll(new TimeValue(60000)).setQuery(qb).setSize(chunkSize).get();

		int counter = 1;
		do {
			System.out.println("processing chunk number " + counter);
			for (SearchHit hit : response.getHits().getHits()) {
				hitIdList.add(hit.getId());
				
				JsonParser parser;
				// String objectId, vernum, pure_id = null;
				String version, release, latest = null;

				try {
					parser = jf.createParser(hit.getSourceAsString());
					JsonNode item = om.readTree(parser);
					//System.out.println(item.get("version").get("objectId").textValue());
						change(item, "objectId");
						change(item, "contentModel");

						//System.out.println(item.get("version").get("objectId").textValue());
					
						indexList.add(hit.getId());
						IndexRequest request = new IndexRequest(destIndex, hit.getType(), hit.getId());
						request.source(item.toString());
						processor.add(request);
						
						
						
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			response = client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute()
					.actionGet();
			counter += 1;

		} while (response.getHits().getHits().length != 0);

		try {
			processor.awaitClose(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long end = System.currentTimeMillis() - start;
		System.out.println("scrolling through " + hitIdList.size() + " docs takes: " + end);
		System.out.println("items added to search index: " + indexList.size());
	}

	public static void scroll(TransportClient client, String index) {

		JsonFactory jf = new JsonFactory();
		ObjectMapper om = new ObjectMapper();

		ArrayList<String> hitIdList = new ArrayList<>();
		ArrayList<String> hitIist_vrl = new ArrayList<>();
		ArrayList<String> hitList_vr = new ArrayList<>();
		ArrayList<String> hitList_vl = new ArrayList<>();

		int chunkSize = 1000;
		System.out.println("Starting scroll with chunks of " + chunkSize);
		long start = System.currentTimeMillis();

		QueryBuilder qb = QueryBuilders.matchAllQuery();

		SearchResponse response = client.prepareSearch(index)
				// .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
				.setScroll(new TimeValue(60000)).setQuery(qb).setSize(chunkSize).setVersion(true).get();

		int counter = 1;
		do {
			System.out.println("processing chunk number " + counter);
			for (SearchHit hit : response.getHits().getHits()) {
				hitIdList.add(hit.getId());
				JsonParser parser;
				String version, release, latest = null;
				try {
					parser = jf.createParser(hit.getSourceAsString());
					JsonNode item = om.readTree(parser);
					version = item.get("version").get("versionNumber").asText();
					release = item.get("latestRelease").get("versionNumber").asText();
					latest = item.get("latestVersion").get("versionNumber").asText();

					System.out.println(version + "   " + release + "   " + latest);

					if (version.equals(release) && version.equals(latest)) {
						hitIist_vrl.add(hit.getId());
					} else {
						if (version.equals(release)) {
							hitList_vr.add(hit.getId());
						} else {
							if (version.equals(latest)) {
								hitList_vl.add(hit.getId());
							}
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			response = client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute()
					.actionGet();
			counter += 1;

		} while (response.getHits().getHits().length != 0);

		long end = System.currentTimeMillis() - start;
		System.out.println("scrolling through " + hitIdList.size() + " docs takes: " + end);
		System.out.println("version=release=latest " + hitIist_vrl.size());
		System.out.println("version=release " + hitList_vr.size());
		System.out.println("version=latest " + hitList_vl.size());
	}

	public static void getInfo(TransportClient tc, String index) {

		ObjectMapper om = new ObjectMapper();
		JsonFactory jf = new JsonFactory();

		QueryBuilder qb = QueryBuilders.matchAllQuery();

		SearchResponse response = tc.prepareSearch(index).setQuery(qb).setSize(3).get();
		
		System.out.println(response.getHits().getTotalHits());

		for (SearchHit hit : response.getHits().getHits()) {
			try {
				JsonParser parser = jf.createParser(hit.getSourceAsString());
				JsonNode item = om.readTree(parser);
				String objectId = item.get("version").get("objectId").textValue();
				objectId = objectId.substring(objectId.indexOf(":") + 1);
				String vernum = item.get("version").get("versionNumber").asText();
				String pure_id = "pure_" + objectId + "_" + vernum;

				System.out.println(hit.getId() + "   " + pure_id);
				
				change(item, "objectId");
				change(item, "contentModel");
				//System.out.println(item.textValue());
				//System.out.println(item.toString());
				//om.writerWithDefaultPrettyPrinter().writeValue(System.out, item);				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public static void change(JsonNode parent, String fieldName) {
        if (parent.has(fieldName)) {
        	String value = parent.get(fieldName).asText();
        	String newValue = "pure_"+value.substring(value.lastIndexOf(":") +1);
        	// System.out.println(value + " is now " + newValue);
            ((ObjectNode) parent).put(fieldName, newValue);
        }

        for (JsonNode child : parent) {
            change(child, fieldName);
        }
	}
        
}
