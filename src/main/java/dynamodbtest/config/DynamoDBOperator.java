package dynamodbtest.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.jayway.awaitility.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient.builder;
import static com.jayway.awaitility.Awaitility.await;
import static com.jayway.awaitility.Duration.ONE_SECOND;

public class DynamoDBOperator {

    private static AmazonDynamoDB client;

    public synchronized static AmazonDynamoDB connect(AWSCredentialsProvider awsCredentialsProvider, ClientConfiguration localClientConfig, String endpoint) {
        client = builder().withClientConfiguration(localClientConfig).withCredentials(awsCredentialsProvider).withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "milkyway")).build();
        return client;
    }

    public static void deleteAllTables() {
        client.listTables().getTableNames().forEach(table -> deleteTable(table));
    }

    public static void deleteTable(String tableName) {
        if (client.listTables().getTableNames().stream().anyMatch(s -> s.equals(tableName))) {
            client.deleteTable(tableName);
            await().pollInterval(ONE_SECOND).atMost(Duration.ONE_MINUTE).until(() -> !tableExists(tableName));
        }
    }

    public static void createTable(String tableName) {
        List<KeySchemaElement> ks = new ArrayList<>();

        ProvisionedThroughput provisionedthroughput = new ProvisionedThroughput(1000L, 1000L);
        String hashKeyName = "id";
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition(hashKeyName, ScalarAttributeType.S));

        ks.add(new KeySchemaElement(hashKeyName, KeyType.HASH));
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(tableName)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(ks)
                .withProvisionedThroughput(provisionedthroughput);
        client.createTable(request);
    }

    public static void shutdown() {
        client.shutdown();
    }

    private static boolean tableExists(String tableName) {
        return client.listTables().getTableNames().stream().anyMatch(s -> s.equals(tableName));
    }
}
