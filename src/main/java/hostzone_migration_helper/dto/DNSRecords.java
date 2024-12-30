package hostzone_migration_helper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DNSRecords {

    @JsonProperty("ResourceRecordSets")
    private List<ResourceRecordSet> resourceRecordSets;

    // Getters and Setters
    public List<ResourceRecordSet> getResourceRecordSets() {
        return resourceRecordSets;
    }

    public void setResourceRecordSets(List<ResourceRecordSet> resourceRecordSets) {
        this.resourceRecordSets = resourceRecordSets;
    }
}

