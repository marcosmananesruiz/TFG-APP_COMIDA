package org.dam2.bomboplatsserver.controller;


import org.dam2.bomboplats.api.User;
import org.dam2.bomboplats.api.login.LoginAttempt;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.modelo.mapper.UserEntityMapper;
import org.dam2.bomboplatsserver.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired private IUserService service;
    @Autowired private UserEntityMapper mapper;
    @Autowired private PasswordEncoder encoder;

    @GetMapping("/login")
    public Mono<Boolean> login(@RequestBody LoginAttempt loginAttempt) {
        return this.service.findByEmail(loginAttempt.email()).map(userEntity -> {
            String storedPassword = userEntity.getPassword();
            return this.encoder.matches(loginAttempt.password(), storedPassword);
        }).switchIfEmpty(Mono.just(false));
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
            String hashedPassword = this.encoder.encode(password);
            userEntity.setPassword(hashedPassword);
            return this.service.register(userEntity);
        });
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Boolean> deleteByID(@PathVariable String id) {
        return this.service.deleteUserByID(id);
    }

    @GetMapping("/get")
    public Flux<User> findAll() {
        return this.mapper.mapFlux(this.service.findAll());
    }
}
