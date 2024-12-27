package hostzone_migration_helper.controller;

import hostzone_migration_helper.service.RecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadJsonFile(@RequestParam("file") MultipartFile file) {
        try {
            recordService.backupFile(file);
            return ResponseEntity.ok("File uploaded and backed up successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process the file: " + e.getMessage());
        }
    }
}
