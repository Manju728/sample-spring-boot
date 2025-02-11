package com.vankay.vankay.controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import javax.net.ssl.SSLContext;


@RestController
@RequestMapping("/")
public class VankayController {
    private static final Logger log = LoggerFactory.getLogger(VankayController.class);

    @GetMapping
    public String home() {
        log.info("Called home route.");
        return "Hello, I am vankay!";
    }

    @GetMapping("/mongo")
    public String health() {
        return "It is not yet implemented";
    }

    @GetMapping("/s3")
    public String test() {
        log.info("Called s3 route.");
        String region = "ap-south-1";
        S3Client s3Client = S3Client.builder().region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create()).build();
        return s3Client.listBuckets().buckets().get(0).name();
    }

    @GetMapping("/secret-manager")
    public String testSecretManager() {
        String region = "ap-south-1";
        String secretName = "test";
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.of(region)).credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            System.out.println(getSecretValueResponse);
        } catch (Exception e) {
            throw e;
        }
        String secret = getSecretValueResponse.secretString();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> secretMap = objectMapper.readValue(secret, Map.class);
            System.out.println(secretMap.get("password"));
            System.out.println(secretMap.get("username"));
            System.setProperty("spring.datasource.password", secretMap.get("password"));
            return secret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return secret;
    }
}