package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplatsserver.service.IS3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;


@Service
public class S3ServiceImpl implements IS3Service {

    @Autowired private S3Presigner presigner;
    /** Nombre del bucket S3 donde se almacenan las imágenes, inyectado desde {@code aws.s3.bucket}. */
    @Value("${aws.s3.bucket}") private String bucket;

    private final Logger LOGGER = LoggerFactory.getLogger(S3ServiceImpl.class);


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
        }).subscribeOn(Schedulers.boundedElastic())
        .doOnError(e -> LOGGER.error("ERROR generando Presigned URL para Bucket S3 {}: {}", this.bucket, e.getMessage()));
    }


    @Override
    public Mono<String> generateUserIconUrl(String userId) {
        return this.generateUpdloadUrl("profile/" + userId + ".jpg");
    }


    @Override
    public Mono<String> generateRestauranteIconUrl(String restauranteId, int index) {
        return this.generateUpdloadUrl("restaurantes/" + restauranteId + "_" + index + ".jpg");
    }



    @Override
    public Mono<String> generatePlatoIconUrl(String platoId) {
        return this.generateUpdloadUrl("platos/" + platoId + ".jpg");
    }
}
