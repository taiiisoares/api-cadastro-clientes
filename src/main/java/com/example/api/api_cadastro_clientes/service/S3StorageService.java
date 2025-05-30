package com.example.api.api_cadastro_clientes.service; // Ou o pacote que você achar melhor para este serviço

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID; 

@Service
public class S3StorageService {

    private final AmazonS3 s3client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    public S3StorageService(AmazonS3 s3client) {
        this.s3client = s3client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        InputStream inputStream = file.getInputStream();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        PutObjectRequest request = new PutObjectRequest(bucketName, uniqueFileName, inputStream, metadata);
        
        s3client.putObject(request);

        URL s3Url = s3client.getUrl(bucketName, uniqueFileName);
        return s3Url.toString();
    }

    public String generatePresignedUrl(String objectKey, int expirationInMinutes){
        if(objectKey == null || objectKey.trim().isEmpty()){
            return null;
        }

        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * expirationInMinutes;
        expiration.setTime(expTimeMillis);

        com.amazonaws.services.s3.model.GeneratePresignedUrlRequest generatePresignedUrlRequest = 
        new com.amazonaws.services.s3.model.GeneratePresignedUrlRequest(bucketName, objectKey)
        .withMethod(com.amazonaws.HttpMethod.GET)
        .withExpiration(expiration);

        URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }
}