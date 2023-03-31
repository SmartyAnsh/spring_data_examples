package com.smartdiscover.config;

import org.springframework.data.elasticsearch.client.erhlc.AbstractElasticsearchConfiguration;

/*public class ElasticConfig extends AbstractElasticsearchConfiguration {

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();

        return RestClients.create(clientConfiguration)
                .rest();
    }
}*/
public class ElasticConfig {

}
