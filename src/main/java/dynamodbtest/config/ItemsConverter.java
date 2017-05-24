package dynamodbtest.config;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.collect.ImmutableMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static java.util.stream.Collectors.toMap;


public class ItemsConverter implements DynamoDBTypeConverter<Map<String, Map<String, AttributeValue>>, Map<String, LocalDate>> {

    private static final String CREATED_AT_FIELD = "created_at";

    @Override
    public Map<String, Map<String, AttributeValue>> convert(Map<String, LocalDate> stockMap) {
        return stockMap.entrySet().stream()
                .collect(toMap(entry -> entry.getKey(), entry -> ImmutableMap.of(
                        CREATED_AT_FIELD, new AttributeValue(entry.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))));

    }

    @Override
    public Map<String, LocalDate> unconvert(Map<String, Map<String, AttributeValue>> stockMap) {
        return stockMap.entrySet().stream()
                .collect(toMap(entry -> (entry.getKey()), entry ->
                        LocalDate.parse(entry.getValue().get(CREATED_AT_FIELD).getS(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
    }

}
