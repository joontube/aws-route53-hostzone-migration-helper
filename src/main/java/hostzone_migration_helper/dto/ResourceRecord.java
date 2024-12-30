package hostzone_migration_helper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceRecord {

    @JsonProperty("Value")
    private String value;

    // Getters and Setters
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
