package de.mpg.mpdl.auth.service.es;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.AliasOrIndex;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.collect.ImmutableOpenMap;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ElasticClusterInfoService {
	
	private static final String MAPPING_JSON_PATH = "/home/frank/data/git/auth/src/main/resources/json/";
	
	private static ClusterStateResponse clusterStateResponse() {
		Client c = ElasticTransportClient.start();
		ClusterStateResponse resp = c.admin().cluster().prepareState()
				.execute().actionGet();
		c.close();
		return resp;
	}
	
	public static void listNodes() {
		
		ClusterStateResponse resp = clusterStateResponse();
		ImmutableOpenMap<String, DiscoveryNode> nodeMap = resp.getState()
				.nodes().getNodes();
		
		Iterator<ObjectObjectCursor<String, DiscoveryNode>> it = nodeMap
				.iterator();
		while (it.hasNext()) {
			ObjectObjectCursor<String, DiscoveryNode> node = it.next();
			System.out.println(node.key + "  " + node.value.getName());
		}
	}
	
	public static List<String> listIndices() {
		
		ArrayList<String> indexNames = new ArrayList<String>();
		ClusterStateResponse resp = clusterStateResponse();
		SortedMap<String, AliasOrIndex> indices = resp.getState().getMetaData()
				.getAliasAndIndexLookup();
		// indices.forEach((k, v) -> System.out.println(k + "   " + v.getIndices().get(0).getCreationDate()));
		indices.forEach((k, v) -> indexNames.add(k));
		return indexNames;
	}

	public static void indexInfo(String indexName, String type) {
		
		ClusterStateResponse resp = clusterStateResponse();
		IndexMetaData idxMeta = resp.getState().getMetaData().index(indexName);
		MappingMetaData mapMeta = idxMeta.mapping(type);
		System.out.println(mapMeta.type());
		ObjectMapper om = new ObjectMapper();
		om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		try {
			//om.writerWithDefaultPrettyPrinter().writeValue(System.out, idxMeta);
			om.writerWithDefaultPrettyPrinter().writeValue(new File(MAPPING_JSON_PATH + "item_mapping.json"), mapMeta.getSourceAsMap());
			
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
