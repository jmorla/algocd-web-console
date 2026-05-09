package com.algocd.webportal.services;

import com.algocd.webportal.exceptions.AlgocdException;
import com.algocd.webportal.exceptions.ErrorReason;
import com.algocd.webportal.util.Reasons;
import com.algocd.webportal.util.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    public FileSystemStorageService(@Value("${algocd.storage.upload-dir}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir);
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new AlgocdException(ErrorReason.INTERNAL_SERVER_ERROR, "Could not initialize storage location: " + uploadDir, e);
        }
    }

    @Override
    public Result<Path> store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Reasons.internalFailure("Failed to store empty file.");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueFilename = UUID.randomUUID() + extension;
            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename)).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                return Reasons.internalFailure("Cannot store file outside current directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return Result.success(destinationFile);
        } catch (IOException e) {
            return Reasons.internalFailure("Failed to store file.", e);
        }
    }
}
