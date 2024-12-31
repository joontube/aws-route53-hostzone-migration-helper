package hostzone_migration_helper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResourceRecordSet {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("TTL")
    private int ttl;

    @JsonProperty("AliasTarget")
    private AliasTarget aliasTarget;

    @JsonProperty("ResourceRecords")
    private List<ResourceRecord> resourceRecords;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public List<ResourceRecord> getResourceRecords() {
        return resourceRecords;
    }

    public void setResourceRecords(List<ResourceRecord> resourceRecords) {
        this.resourceRecords = resourceRecords;
    }

    public AliasTarget getAliasTarget() {
        return aliasTarget;
    }

    public void setAliasTarget(AliasTarget aliasTarget) {
        this.aliasTarget = aliasTarget;
    }
}

