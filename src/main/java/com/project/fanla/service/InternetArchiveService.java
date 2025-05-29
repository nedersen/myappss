package com.project.fanla.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.UUID;

@Service
public class InternetArchiveService {

    @Value("${internetarchive.access-key}")
    private String accessKey;

    @Value("${internetarchive.secret-key}")
    private String secretKey;
    
    /**
     * Uploads a file to Internet Archive and returns the download URL
     * 
     * @param file The file to upload
     * @return The download URL for the uploaded file
     * @throws IOException If an error occurs during upload
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // Generate a unique identifier
        String identifier = "audio-" + UUID.randomUUID().toString();
        
        // Get original filename and clean it for safe usage
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "audio-" + System.currentTimeMillis() + ".mp3";
        }
        
        // Clean the filename for URL safety
        String cleanFileName = cleanFileName(originalFilename);
        
        // URL encode the filename for the upload URL
        String encodedFileName = URLEncoder.encode(cleanFileName, StandardCharsets.UTF_8.toString());

        // Create HTTP client
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create upload request to S3-like endpoint
            String uploadUrl = String.format("https://s3.us.archive.org/%s/%s", identifier, encodedFileName);
            HttpPut uploadRequest = new HttpPut(uploadUrl);

            // Set required headers
            uploadRequest.setHeader("Authorization", "LOW " + accessKey + ":" + secretKey);
            uploadRequest.setHeader("x-archive-auto-make-bucket", "1");
            uploadRequest.setHeader("x-archive-meta-collection", "opensource_audio");
            uploadRequest.setHeader("x-archive-meta-mediatype", "audio");
            uploadRequest.setHeader("x-archive-meta-title", cleanFileName);
            uploadRequest.setHeader("Content-Type", file.getContentType());

            // Set the file content
            uploadRequest.setEntity(new InputStreamEntity(file.getInputStream(), file.getSize()));

            // Execute upload request with retry logic
            int maxRetries = 3;
            int retryCount = 0;

            while (retryCount < maxRetries) {
                try (CloseableHttpResponse response = httpClient.execute(uploadRequest)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    
                    if (statusCode == 200 || statusCode == 201) {
                        // Successful upload, return the download URL in the format requested
                        // Format: https://ia800708.us.archive.org/21/items/audio-UUID/filename.mp3
                        return String.format("https://ia800708.us.archive.org/21/items/%s/%s", identifier, cleanFileName);
                    }
                    
                    retryCount++;
                    if (retryCount == maxRetries) {
                        throw new IOException("Failed to upload file to Internet Archive after " + maxRetries + 
                            " attempts. Status code: " + statusCode);
                    }
                    
                    // Wait before retrying with exponential backoff
                    Thread.sleep(1000L * retryCount);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Upload interrupted", e);
                }
            }
            
            throw new IOException("Failed to upload file to Internet Archive");
        }
    }
    
    /**
     * Cleans a filename to make it URL-safe and compatible with Internet Archive
     * 
     * @param fileName The original filename
     * @return A cleaned filename safe for URLs and Internet Archive
     */
    private String cleanFileName(String fileName) {
        // Normalize Unicode characters (convert accented characters to ASCII equivalents)
        String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        
        // Replace spaces with underscores
        normalized = normalized.replaceAll("\\s+", "_");
        
        // Replace special characters with underscores
        normalized = normalized.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        // Replace multiple consecutive underscores with a single one
        normalized = normalized.replaceAll("_+", "_");
        
        // Ensure the filename doesn't start or end with an underscore
        normalized = normalized.replaceAll("^_|_$", "");
        
        return normalized;
    }
}
