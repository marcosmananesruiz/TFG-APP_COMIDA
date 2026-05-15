package org.dam2.bomboplatsserver.service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Interfaz del servicio de Amazon S3.
 * Genera URLs prefirmadas para que los clientes puedan subir
 * imágenes directamente al bucket sin necesidad de credenciales propias.
 */
public interface IS3Service {


    /**
     * Genera una URL prefirmada de subida (PUT) para una clave concreta del bucket.
     * La URL tiene una validez de 5 minutos y acepta únicamente imágenes JPEG.
     * La operación se ejecuta en un hilo de {@link Schedulers#boundedElastic()}
     * para no bloquear el hilo reactivo.
     *
     * @param key ruta del objeto dentro del bucket (ej: "profile/U00001.jpg")
     * @return Mono con la URL prefirmada como String
     */
    Mono<String> generateUpdloadUrl(String key);

    /**
     * Genera una URL prefirmada para subir la imagen de perfil de un usuario.
     * La imagen se almacena en la ruta {@code profile/{userId}.jpg}.
     *
     * @param userId identificador del usuario
     * @return Mono con la URL prefirmada
     */
    Mono<String> generateUserIconUrl(String userId);

    /**
     * Genera una URL prefirmada para subir una imagen de un restaurante.
     * La imagen se almacena en la ruta {@code restaurantes/{restauranteId}_{index}.jpg},
     * permitiendo múltiples fotos por restaurante mediante el índice.
     *
     * @param restauranteId identificador del restaurante
     * @param index         índice de la imagen (0 para la principal)
     * @return Mono con la URL prefirmada
     */
    Mono<String> generateRestauranteIconUrl(String restauranteId, int index);

    /**
     * Genera una URL prefirmada para subir la imagen de un plato.
     * La imagen se almacena en la ruta {@code platos/{platoId}.jpg}.
     *
     * @param platoId identificador del plato
     * @return Mono con la URL prefirmada
     */
    Mono<String> generatePlatoIconUrl(String platoId);
}
