package org.dam2.bomboplatsserver.service;

import reactor.core.publisher.Mono;

public interface IS3Service {

    Mono<String> generateUpdloadUrl(String key);
    Mono<String> generateUserIconUrl(String userId);
    Mono<String> generateRestauranteIconUrl(String restauranteId, int index);
    Mono<String> generatePlatoIconUrl(String platoId);
}
