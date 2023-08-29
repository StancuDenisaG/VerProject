package org.example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVIndexer {

    public static void main(String[] args) throws IOException {
        try (RestHighLevelClient client = createClient()) {
            indexCSVData(client, "merged-data4.csv");
        }
    }

    public static RestHighLevelClient createClient() {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("elasticsearch", 9200, "http"));
        return new RestHighLevelClient(builder);
    }

    private static void indexCSVData(RestHighLevelClient client, String csvFilePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 3) {
                    String domain = fields[0];
                    String number = fields[1];
                    String socials = fields[2];

                    IndexRequest request = new IndexRequest("csv_data")
                            .source("domain", domain, "number", number, "socials", socials);

                    IndexResponse response = client.index(request,  RequestOptions.DEFAULT);
                    System.out.println("Indexed document with ID: " + response.getId());
                }
            }
        }
    }
}
