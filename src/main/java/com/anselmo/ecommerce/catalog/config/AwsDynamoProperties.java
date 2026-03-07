package com.anselmo.ecommerce.catalog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws")
public class AwsDynamoProperties {

    private String region;
    private Dynamo dynamodb = new Dynamo();

    @Getter
    @Setter
    public static class Dynamo {
        private String tableName;
        private String skuIndexName;
    }
}
