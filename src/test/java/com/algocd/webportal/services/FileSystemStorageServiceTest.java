package com.algocd.webportal.services;

import com.algocd.webportal.exceptions.AlgocdException;
import com.algocd.webportal.util.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FileSystemStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileSystemStorageService storageService;
    private String uploadDir;

    @BeforeEach
    void setUp() {
        uploadDir = tempDir.resolve("uploads").toString();
        storageService = new FileSystemStorageService(uploadDir);
    }

    @Test
    void storeValidFile_ReturnsSuccess() {
        MockMultipartFile file = new MockMultipartFile("file", "test.mql5", "text/plain", "content".getBytes());
        Result<Path> result = storageService.store(file);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).exists();
        assertThat(result.getValue().getFileName().toString()).endsWith(".mql5");
    }

    @Test
    void store_WithDirectoryTraversal_ReturnsFailure() {
        // Filename that tries to traverse OUTSIDE the root via the extension logic
        // extension will be ".mql5/../../outside"
        MockMultipartFile file = new MockMultipartFile("file", "malicious.mql5/../../outside", "text/plain", "content".getBytes());
        Result<Path> result = storageService.store(file);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getMessage()).contains("Cannot store file outside current directory");
    }

    @Test
    void storeEmptyFile_ReturnsFailure() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.mql5", "text/plain", new byte[0]);
        Result<Path> result = storageService.store(file);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getMessage()).contains("Failed to store empty file");
    }

    @Test
    void storeValidFile_WritesCorrectContent() throws IOException {
        byte[] content = "Hello World".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "hello.mql5", "text/plain", content);
        Result<Path> result = storageService.store(file);

        assertThat(result.isSuccess()).isTrue();
        assertThat(Files.readAllBytes(result.getValue())).isEqualTo(content);
    }

    @Test
    void constructor_CreatesStorageDirectory() {
        Path newUploadDir = tempDir.resolve("new-uploads");
        new FileSystemStorageService(newUploadDir.toString());
        assertThat(newUploadDir).exists().isDirectory();
    }

    @Test
    void store_ConsecutiveUploads_GeneratesUniqueNames() {
        MockMultipartFile file1 = new MockMultipartFile("file", "test.mql5", "text/plain", "content1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "test.mql5", "text/plain", "content2".getBytes());

        Result<Path> result1 = storageService.store(file1);
        Result<Path> result2 = storageService.store(file2);

        assertThat(result1.getValue()).isNotEqualTo(result2.getValue());
    }

    @Test
    void store_IoException_ReturnsFailure() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.mql5");
        when(file.getInputStream()).thenThrow(new IOException("Disk full"));

        Result<Path> result = storageService.store(file);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getMessage()).contains("Failed to store file");
    }

    @Test
    void store_FileWithoutExtension_HandlesCorrectly() {
        MockMultipartFile file = new MockMultipartFile("file", "noextension", "text/plain", "content".getBytes());
        Result<Path> result = storageService.store(file);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getFileName().toString()).doesNotContain(".");
    }

    @Test
    void constructor_CannotCreateDirectory_ThrowsException() {
        // Use a path that is likely to fail (e.g., a file exists with the same name as the dir)
        Path filePath = tempDir.resolve("already-a-file");
        try {
            Files.write(filePath, "content".getBytes());
        } catch (IOException ignored) {}

        assertThatThrownBy(() -> new FileSystemStorageService(filePath.resolve("subdir").toString()))
                .isInstanceOf(AlgocdException.class)
                .hasMessageContaining("Could not initialize storage location");
    }

    @Test
    void store_NormalizesPathCorrectly() {
        // This test ensures that even with complex names, the normalization logic keeps it in root
        MockMultipartFile file = new MockMultipartFile("file", "././test.mql5", "text/plain", "content".getBytes());
        Result<Path> result = storageService.store(file);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getParent()).isEqualTo(Paths.get(uploadDir).toAbsolutePath());
    }
}
