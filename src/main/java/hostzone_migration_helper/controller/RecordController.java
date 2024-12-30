package hostzone_migration_helper.controller;

import hostzone_migration_helper.service.DNSRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    // Service to handle DNS record processing
    private final DNSRecordService dnsRecordService;

    // Constructor to inject DNSRecordService into the controller
    public RecordController(DNSRecordService dnsRecordService) {
        this.dnsRecordService = dnsRecordService;
    }

    /**
     * Endpoint to filter DNS records from an uploaded file.
     * This endpoint accepts a file, processes the DNS records, and returns the path of the filtered result.
     *
     * @param file The file containing the DNS records to be processed
     * @return A response indicating the result of the filtering process
     */
    @PostMapping("/filter")
    public ResponseEntity<String> filterDNSRecords(@RequestParam("file") MultipartFile file) {
        try {
            // Process the uploaded file and filter the DNS records
            Path resultFilePath = dnsRecordService.processFilteredRecords(file);

            // Return a success message with the path to the filtered file
            return ResponseEntity.ok("Filtered JSON saved at: " + resultFilePath);
        } catch (IOException e) {
            // Handle any IOException that occurs during the file processing and return an error response
            return ResponseEntity.status(500).body("Failed to process file: " + e.getMessage());
        }
    }
}
