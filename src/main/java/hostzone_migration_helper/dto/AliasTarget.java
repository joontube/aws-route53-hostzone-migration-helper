package hostzone_migration_helper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AliasTarget {

    @JsonProperty("HostedZoneId")
    private String hostedZoneId;

    @JsonProperty("DNSName")
    private String dnsName;

    @JsonProperty("EvaluateTargetHealth")
    private boolean evaluateTargetHealth;

    // Getters and setters
    public String getHostedZoneId() {
        return hostedZoneId;
    }

    public void setHostedZoneId(String hostedZoneId) {
        this.hostedZoneId = hostedZoneId;
    }

    public String getDnsName() {
        return dnsName;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    public boolean isEvaluateTargetHealth() {
        return evaluateTargetHealth;
    }

    public void setEvaluateTargetHealth(boolean evaluateTargetHealth) {
        this.evaluateTargetHealth = evaluateTargetHealth;
    }
}
