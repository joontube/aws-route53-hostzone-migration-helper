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
     * Endpoint to filter DNS records from uploaded files.
     * This endpoint accepts multiple JSON files, processes the DNS records in each file,
     * and returns the paths of the filtered results.
     *
     * @param files An array of files containing the DNS records to be processed
     * @return A response indicating the result of the filtering process, including the paths of filtered files
     */
    @PostMapping("/filter")
    public ResponseEntity<String> filterDNSRecords(@RequestParam("files") MultipartFile[] files) {
        try {
            // Check if no files were uploaded
            if (files == null || files.length == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No files were uploaded. Please upload valid JSON files.");
            }

            // StringBuilder to accumulate paths of processed files
            StringBuilder resultPaths = new StringBuilder();

            // Process each file independently
            for (MultipartFile file : files) {
                // Validate if the file is empty
                if (file.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One of the uploaded files is empty. Please provide valid JSON files.");
                }

                // Validate if the file has a .json extension
                if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".json")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Invalid file format. All files must have a .json extension.");
                }

                // Process the valid file and filter its contents
                Path resultFilePath = dnsRecordService.processFile(file);

                // Append the path of the processed file to the response message
                resultPaths.append("Filtered JSON saved at: ").append(resultFilePath).append("\n");
            }

            // Return the paths of all processed files
            return ResponseEntity.ok(resultPaths.toString());

        } catch (IOException e) {
            // Handle errors related to file I/O
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File processing failed due to an I/O error: " + e.getMessage());
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}
