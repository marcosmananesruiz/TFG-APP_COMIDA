package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplatsserver.service.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
public class S3ServiceImpl implements IS3Service {

    @Autowired private S3Presigner presigner;
    @Value("${aws.s3.bucket}") private String bucket;


    @Override
    public Mono<String> generateUpdloadUrl(String key) {
        return Mono.fromCallable(() -> {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(this.bucket)
                    .key(key)
                    .contentType("image/jpeg")
                    .build();

            PutObjectPresignRequest presignRequest =
                    PutObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(5))
                            .putObjectRequest(objectRequest)
                            .build();

            PresignedPutObjectRequest presignedRequest =
                    presigner.presignPutObject(presignRequest);

            return presignedRequest.url().toString();
        });
    }


    public Mono<String> generateUserIconUrl(String userId) {
        return this.generateUpdloadUrl("profile/" + userId + ".jpg");
    }
}
