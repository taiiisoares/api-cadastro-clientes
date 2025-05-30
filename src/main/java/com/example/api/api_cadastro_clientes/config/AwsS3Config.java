package com.example.api.api_cadastro_clientes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsS3Config {
    @Value("${AWS_REGION}")
    private String awsRegion;

    @Bean
    public AmazonS3 s3Client(){
        return AmazonS3ClientBuilder.standard()
        .withRegion(awsRegion)
        .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
        .build();
    }
}
