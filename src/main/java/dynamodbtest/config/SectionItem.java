package dynamodbtest.config;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@DynamoDBTable(tableName = "section_items")
public final class SectionItem {

    @DynamoDBHashKey(attributeName = "id")
    private String section;

    @DynamoDBAttribute(attributeName = "items")
    @DynamoDBTypeConverted(converter = ItemsConverter.class)
    private Map<String, LocalDate> items;

    @DynamoDBVersionAttribute
    private Long version;

    public SectionItem(String section, Map<String, LocalDate> newMap, Long version) {
        this.version = version;
        this.section = section;
        this.items = newMap;
    }

    public String getSection() {
        return section;
    }

    public Map<String, LocalDate> getItems() {
        return items;
    }

    public Long getVersion() {
        return version;
    }

    public SectionItem(String section, Map<String, LocalDate> items) {
        this.section = section;
        this.items = items;
    }

    public SectionItem withNewItems(String key, LocalDate time) {
        Map<String, LocalDate> newMap = new HashMap<>();
        newMap.putAll(items);
        newMap.put(key, time);
        return new SectionItem(section, newMap, version);
    }

    public SectionItem() {
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setItems(Map<String, LocalDate> items) {
        this.items = items;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionItem sectionItem = (SectionItem) o;
        return Objects.equals(section, sectionItem.section) &&
                Objects.equals(items, sectionItem.items) &&
                Objects.equals(version, sectionItem.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(section, items, version);
    }
}
