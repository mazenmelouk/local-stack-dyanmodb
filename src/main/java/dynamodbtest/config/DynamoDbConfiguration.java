package dynamodbtest.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;

@Configuration
public class DynamoDbConfiguration {
    private static final ClientConfiguration LOCAL_CLIENT_CONFIG = new ClientConfiguration().withProtocol(Protocol.HTTP);

    private static AWSCredentialsProvider credentialsProvider() {
        return new AWSCredentialsProvider() {

            public AWSCredentials getCredentials() {
                return new BasicAWSCredentials("key", "secret");
            }

            public void refresh() {

            }
        };
    }

    @Bean
    public DynamoDB dynamoDB(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDB(amazonDynamoDB);
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        AmazonDynamoDB client = DynamoDBOperator.connect(credentialsProvider(), LOCAL_CLIENT_CONFIG, "http://localhost:8000/");
        DynamoDBOperator.deleteAllTables();
        DynamoDBOperator.createTable("section_items");
return client;
    }

    @Bean
    public DynamoDBMapperConfig dynamoDBMapperConfig(TableNameOverride tableNameOverrider) {
        return DynamoDBMapperConfig.builder().withTableNameOverride(tableNameOverrider).build();
    }

    @Bean
    public TableNameOverride tableNameOverrider() {
        return TableNameOverride.withTableNamePrefix("");
    }
}
