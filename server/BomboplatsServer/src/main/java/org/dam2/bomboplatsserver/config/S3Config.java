package org.dam2.bomboplatsserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.support.SimpleTriggerContext;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * Configuración de los clientes de Amazon S3.
 * Inicializa el cliente asíncrono para operaciones sobre el bucket
 * y el presigner para la generación de URLs prefirmadas.
 * La región se obtiene del archivo de propiedades de la aplicación.
 */
@Configuration
public class S3Config {
    /**
     * Región de AWS donde se encuentra el bucket S3,
     * inyectada desde la propiedad {@code aws.region}.
     */
    @Value("${aws.region}")
    private String region;

    /**
     * Cliente asíncrono de S3 para operaciones no bloqueantes sobre el bucket.
     * Utiliza las credenciales por defecto del entorno (variables de entorno o
     * perfil de AWS).
     *
     * @return instancia configurada de {@link S3AsyncClient}
     */
    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.builder()
                .region(Region.of(this.region))
                .build();
    }

    /**
     * Presigner de S3 para generar URLs prefirmadas que permiten
     * subir o descargar objetos sin necesidad de credenciales directas.
     *
     * @return instancia configurada de {@link S3Presigner}
     */
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .build();
    }
}
