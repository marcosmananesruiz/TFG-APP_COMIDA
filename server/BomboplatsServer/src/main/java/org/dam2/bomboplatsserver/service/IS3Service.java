package org.dam2.bomboplatsserver.service;

import reactor.core.publisher.Mono;

public interface IS3Service {

    Mono<String> generateUpdloadUrl(String key);
    Mono<String> generateUserIconUrl(String userId);
}
