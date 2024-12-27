package hostzone_migration_helper.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class RecordService {

    private static final String BACKUP_FILE_NAME = "migrate_records.json";
    private final String backupFolderPath;

    public RecordService(@Value("${backup.folder.path}") String backupFolderPath) {
        this.backupFolderPath = backupFolderPath;
    }

    public void backupFile(MultipartFile file) throws IOException {
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".json")) {
            throw new IllegalArgumentException("Invalid file format. Please upload a JSON file.");
        }

        // 백업 디렉토리 생성
        Path backupDir = Paths.get(backupFolderPath);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
            System.out.println("Backup directory created: " + backupDir);
        }

        // 파일 백업
        Path backupFilePath = backupDir.resolve(BACKUP_FILE_NAME);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Backup file copied: " + backupFilePath);
        }
    }
}
