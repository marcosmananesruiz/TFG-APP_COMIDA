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

/**
 * Implementación del servicio de Amazon S3.
 * Genera URLs prefirmadas para que los clientes puedan subir
 * imágenes directamente al bucket sin necesidad de credenciales propias.
 */
@Service
public class S3ServiceImpl implements IS3Service {

    @Autowired private S3Presigner presigner;
    /** Nombre del bucket S3 donde se almacenan las imágenes, inyectado desde {@code aws.s3.bucket}. */
    @Value("${aws.s3.bucket}") private String bucket;

    private final Logger LOGGER = LoggerFactory.getLogger(S3ServiceImpl.class);

    /**
     * Genera una URL prefirmada de subida (PUT) para una clave concreta del bucket.
     * La URL tiene una validez de 5 minutos y acepta únicamente imágenes JPEG.
     * La operación se ejecuta en un hilo de {@link Schedulers#boundedElastic()}
     * para no bloquear el hilo reactivo.
     *
     * @param key ruta del objeto dentro del bucket (ej: "profile/U00001.jpg")
     * @return Mono con la URL prefirmada como String
     */
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

    /**
     * Genera una URL prefirmada para subir la imagen de perfil de un usuario.
     * La imagen se almacena en la ruta {@code profile/{userId}.jpg}.
     *
     * @param userId identificador del usuario
     * @return Mono con la URL prefirmada
     */
    @Override
    public Mono<String> generateUserIconUrl(String userId) {
        return this.generateUpdloadUrl("profile/" + userId + ".jpg");
    }

    /**
     * Genera una URL prefirmada para subir una imagen de un restaurante.
     * La imagen se almacena en la ruta {@code restaurantes/{restauranteId}_{index}.jpg},
     * permitiendo múltiples fotos por restaurante mediante el índice.
     *
     * @param restauranteId identificador del restaurante
     * @param index         índice de la imagen (0 para la principal)
     * @return Mono con la URL prefirmada
     */
    @Override
    public Mono<String> generateRestauranteIconUrl(String restauranteId, int index) {
        return this.generateUpdloadUrl("restaurantes/" + restauranteId + "_" + index + ".jpg");
    }


    /**
     * Genera una URL prefirmada para subir la imagen de un plato.
     * La imagen se almacena en la ruta {@code platos/{platoId}.jpg}.
     *
     * @param platoId identificador del plato
     * @return Mono con la URL prefirmada
     */
    @Override
    public Mono<String> generatePlatoIconUrl(String platoId) {
        return this.generateUpdloadUrl("platos/" + platoId + ".jpg");
    }
}
