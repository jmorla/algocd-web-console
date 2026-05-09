package com.algocd.webportal.services;

import com.algocd.webportal.util.Result;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface StorageService {
    /**
     * Stores a multipart file and returns the path to the stored file wrapped in a Result.
     *
     * @param file the multipart file to store
     * @return a Result containing the path to the stored file or an error
     */
    Result<Path> store(MultipartFile file);
}
