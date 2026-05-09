package com.algocd.webportal.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class DigestUtil {

    private DigestUtil() {
        // Utility class
    }

    /**
     * Calculates the SHA-256 digest of a file.
     *
     * @param path the path to the file
     * @return a Result containing the digest as a hex string prefixed with "sha256:" or an error
     */
    public static Result<String> calculateSha256(Path path) {
        try (InputStream is = Files.newInputStream(path)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            return Result.success("sha256:" + HexFormat.of().formatHex(digest.digest()));
        } catch (IOException e) {
            return Result.failure(e);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is a mandatory algorithm in every Java implementation
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
