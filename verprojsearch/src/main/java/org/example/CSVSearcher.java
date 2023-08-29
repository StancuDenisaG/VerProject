package org.example;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

import static org.example.CSVIndexer.createClient;

public class CSVSearcher {

    public static void main(String[] args) throws IOException {
        try (RestHighLevelClient client = createClient()) {
            searchCSVData(client, "example.com");
        }
    }

    private static void searchCSVData(RestHighLevelClient client, String searchQuery) throws IOException {
        SearchRequest searchRequest = new SearchRequest("csv_data");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("domain", searchQuery));
        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        response.getHits().forEach(hit -> {
            System.out.println("Matched Document ID: " + hit.getId());
            System.out.println("Source: " + hit.getSourceAsString());
        });
    }
}
