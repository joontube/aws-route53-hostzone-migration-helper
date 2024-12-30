package hostzone_migration_helper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hostzone_migration_helper.dto.DNSRecords;
import hostzone_migration_helper.dto.ResourceRecordSet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DNSRecordService {

    private final ObjectMapper objectMapper;

    // Constants for the output folder and file name suffix
    private static final String OUTPUT_FOLDER = "output";
    private static final String FILTERED_FILE_SUFFIX = "_filtered.json";

    public DNSRecordService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Processes the uploaded file and filters the necessary data.
     *
     * @param file The uploaded MultipartFile
     * @return The path of the result file containing the filtered data
     * @throws IOException If an error occurs while processing the file
     */
    public Path processFilteredRecords(MultipartFile file) throws IOException {
        // Read the uploaded MultipartFile as an InputStream and convert it to a Java object
        InputStream inputStream = file.getInputStream();
        DNSRecords dnsRecords = objectMapper.readValue(inputStream, DNSRecords.class);

        // Filter out records of type "NS" or "SOA"
        List<ResourceRecordSet> filteredRecords = dnsRecords.getResourceRecordSets().stream()
                .filter(record -> !"NS".equalsIgnoreCase(record.getType()) && !"SOA".equalsIgnoreCase(record.getType()))
                .collect(Collectors.toList());

        // Set the filtered records back to the DNSRecords object
        dnsRecords.setResourceRecordSets(filteredRecords);

        // Set the output folder path
        Path outputDir = Paths.get(OUTPUT_FOLDER);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir); // Create the output folder if it does not exist
            System.out.println("Output directory created: " + outputDir);
        }

        // Set the result file path (save within the output folder)
        Path resultFilePath = outputDir.resolve(file.getOriginalFilename().replace(".json", FILTERED_FILE_SUFFIX));

        // Write the Java object to a formatted JSON file
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(resultFilePath.toFile(), dnsRecords);

        System.out.println("Filtered file saved: " + resultFilePath);

        return resultFilePath;
    }
}
