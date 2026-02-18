package org.dam2.bomboplatsserver.controller;


import org.dam2.bomboplats.api.User;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.modelo.mapper.UserEntityMapper;
import org.dam2.bomboplatsserver.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired private IUserService service;
    @Autowired private UserEntityMapper mapper;

    @GetMapping("/login") // /users/login?email=...&password=...
    public Mono<Boolean> login(@RequestParam String email, @RequestParam String password) { // Por seguridad consideraba crear un objeto de LoginAttempt y que se envie por Json toda la info en vez de tenerla en el link
        if (email.equalsIgnoreCase("testeo@bomboplats.org") && password.equalsIgnoreCase("1234")) // Lo malo es que habria que actualizar la API
            return Mono.just(true);
        return Mono.just(false);
    }

    @GetMapping("/get/{id}") // /users/{id}
    public Mono<User> getByID(@PathVariable String id) {
        Mono<UserEntity> monoEntity = this.service.findByID(id);
        return this.mapper.unmap(monoEntity);
    }

    @PostMapping("/register") // Se pasa por json con el body
    public Mono<Boolean> registerUser(@RequestBody User user, @RequestParam String password) {
        Mono<UserEntity> entity = this.mapper.map(Mono.just(user));
        return entity.flatMap(userEntity -> {
            userEntity.setPassword(password);
            return this.service.register(userEntity);
        });
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Boolean> deleteByID(@PathVariable String id) {
        return this.service.deleteUserByID(id);
    }
}
