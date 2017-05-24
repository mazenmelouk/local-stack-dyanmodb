package dynamodbtest;

import dynamodbtest.config.SectionItem;
import dynamodbtest.config.SectionItemRepository;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDynamoDBRepositories(
        dynamoDBMapperConfigRef = "dynamoDBMapperConfig",
        basePackageClasses = {SectionItem.class, SectionItemRepository.class}
)
@ComponentScan
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);

    }
}
