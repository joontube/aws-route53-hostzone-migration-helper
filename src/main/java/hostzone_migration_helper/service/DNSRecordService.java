package hostzone_migration_helper.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hostzone_migration_helper.dto.AliasTarget;
import hostzone_migration_helper.dto.DNSRecords;
import hostzone_migration_helper.dto.ResourceRecordSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DNSRecordService {

    private final ObjectMapper objectMapper;

    @Value("${dns.new-hosted-zone-id}")
    private String newHostedZoneId;

    private static final String OUTPUT_FOLDER = "output";
    private static final String FILTERED_FILE_SUFFIX = "_filtered.json";
    private static final String REMOVED_FILE_NAME = "removed_traffic_policy_records.json";

    public DNSRecordService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public Path processFile(MultipartFile file) throws Exception {
        // Read the uploaded file into a DNSRecords object
        InputStream inputStream = file.getInputStream();
        DNSRecords dnsRecords = objectMapper.readValue(inputStream, DNSRecords.class);

        // Step 1: Filter out records of type "NS" or "SOA"
        List<ResourceRecordSet> filteredRecords = dnsRecords.getResourceRecordSets().stream()
                .filter(record -> !"NS".equalsIgnoreCase(record.getType()) && !"SOA".equalsIgnoreCase(record.getType()))
                .collect(Collectors.toList());

        //TODO
        // Step 2: Remove alias records routing to a traffic policy instance and save to a separate file

//        List<ResourceRecordSet> trafficPolicyRecords = filteredRecords.stream()
//                .filter(record -> record.getAliasTarget() != null && record.getAliasTarget().getDnsName() != null && record.getAliasTarget().getDnsName().contains("trafficpolicy"))
//                .collect(Collectors.toList());
//        filteredRecords.removeAll(trafficPolicyRecords);
//        saveRemovedTrafficPolicyRecords(trafficPolicyRecords);

        // Step 3: Replace old HostedZoneID with new HostedZoneID in Alias records
        replaceHostedZoneId(filteredRecords);

        // Step 4: Sort records so ALIAS records are at the end
        List<ResourceRecordSet> sortedRecords = sortRecords(filteredRecords);

        // Transform to "Changes" structure
        ObjectNode transformedJson = transformToActionResourceRecordSet(sortedRecords);

        // Save the output to a file
        return saveToFile(file, transformedJson);
    }

    private void saveRemovedTrafficPolicyRecords(List<ResourceRecordSet> trafficPolicyRecords) throws IOException {
        Path outputDir = Paths.get(OUTPUT_FOLDER);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
        Path removedFilePath = outputDir.resolve(REMOVED_FILE_NAME);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(removedFilePath.toFile(), trafficPolicyRecords);
        System.out.println("Removed traffic policy records saved to: " + removedFilePath);
    }

    private void replaceHostedZoneId(List<ResourceRecordSet> records) throws Exception {


        for (ResourceRecordSet record : records) {
            if (record.getAliasTarget() != null) {

                AliasTarget aliasTarget = record.getAliasTarget();
                if(aliasTarget.getHostedZoneId() != null && newHostedZoneId.isEmpty()) {
                    throw new Exception("Set hosted zone id in application.yaml file");
                }
                if (aliasTarget.getDnsName() != null) {
                    aliasTarget.setHostedZoneId(newHostedZoneId);
                }
            }
        }
    }


    private List<ResourceRecordSet> sortRecords(List<ResourceRecordSet> records) {
        List<ResourceRecordSet> aliasRecords = records.stream()
                .filter(record -> record.getAliasTarget() != null)
                .collect(Collectors.toList());

        List<ResourceRecordSet> nonAliasRecords = records.stream()
                .filter(record -> record.getAliasTarget() == null)
                .collect(Collectors.toList());

        List<ResourceRecordSet> sortedRecords = new ArrayList<>();
        sortedRecords.addAll(nonAliasRecords);
        sortedRecords.addAll(aliasRecords);
        return sortedRecords;
    }

    protected ObjectNode transformToActionResourceRecordSet(List<ResourceRecordSet> sortedRecords) {
        ObjectNode transformedJson = objectMapper.createObjectNode();
        ArrayNode changesArray = objectMapper.createArrayNode();

        for (ResourceRecordSet record : sortedRecords) {
            ObjectNode changeObject = objectMapper.createObjectNode();
            changeObject.put("Action", "CREATE");

            ObjectNode resourceRecordSet = objectMapper.createObjectNode();
            resourceRecordSet.put("Name", record.getName());
            resourceRecordSet.put("Type", record.getType());

            if (record.getAliasTarget() != null) {
                // If AliasTarget exists, TTL should not be included
                ObjectNode aliasTargetNode = objectMapper.createObjectNode();
                aliasTargetNode.put("HostedZoneId", record.getAliasTarget().getHostedZoneId());
                aliasTargetNode.put("DNSName", record.getAliasTarget().getDnsName());
                aliasTargetNode.put("EvaluateTargetHealth", record.getAliasTarget().isEvaluateTargetHealth());
                resourceRecordSet.set("AliasTarget", aliasTargetNode);
            } else {
                // Include TTL only if AliasTarget does not exist
                if (record.getTtl() > 0) {
                    resourceRecordSet.put("TTL", record.getTtl());
                } else {
                    resourceRecordSet.put("TTL", 300); // Default TTL
                }
                resourceRecordSet.set("ResourceRecords", objectMapper.valueToTree(record.getResourceRecords()));
            }

            changeObject.set("ResourceRecordSet", resourceRecordSet);
            changesArray.add(changeObject);
        }

        transformedJson.set("Changes", changesArray);
        return transformedJson;
    }

    private Path saveToFile(MultipartFile file, ObjectNode transformedJson) throws IOException {
        Path outputDir = Paths.get(OUTPUT_FOLDER);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
            System.out.println("Output directory created: " + outputDir);
        }

        Path resultFilePath = outputDir.resolve(Objects.requireNonNull(file.getOriginalFilename()).replace(".json", FILTERED_FILE_SUFFIX));
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(resultFilePath.toFile(), transformedJson);

        System.out.println("Filtered and transformed file saved: " + resultFilePath);
        return resultFilePath;
    }
}
