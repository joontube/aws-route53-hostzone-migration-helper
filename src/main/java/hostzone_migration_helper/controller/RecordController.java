package hostzone_migration_helper.controller;

import hostzone_migration_helper.service.DNSRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

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
            // Check if the file is empty
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The uploaded file is empty. Please provide a valid JSON file.");
            }

            // Check if the file has a valid JSON extension
            if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".json")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Invalid file format. Please upload a file with a .json extension.");
            }

            // Process the uploaded file and filter the DNS records
            Path resultFilePath = dnsRecordService.processFilteredRecords(file);

            // Return a success message with the path to the filtered file
            return ResponseEntity.ok("Filtered JSON saved at: " + resultFilePath);
        } catch (IOException e) {
            // Handle IO exceptions during file processing
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File processing failed due to an I/O error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Handle cases where the JSON structure does not match the expected DTO
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The uploaded JSON file does not match the expected format: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}
