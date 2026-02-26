package org.dam2.bomboplatsserver.controller;


import org.dam2.bomboplats.api.User;
import org.dam2.bomboplats.api.login.LoginAttempt;
import org.dam2.bomboplats.api.login.UserRegister;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.modelo.mapper.UserEntityMapper;
import org.dam2.bomboplatsserver.service.IS3Service;
import org.dam2.bomboplatsserver.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired private IUserService service;
    @Autowired private UserEntityMapper mapper;
    @Autowired private PasswordEncoder encoder;
    @Autowired private IS3Service s3Service;
    private final String DEFAULT_ICON = "profile/default.jpg"; // En el almacenamiento de imagenes, tendremos una por defecto y palante

    @PutMapping("/login")
    public Mono<Boolean> login(@RequestBody LoginAttempt loginAttempt) {
        return this.service.findByEmail(loginAttempt.email()).map(userEntity -> {
            String storedPassword = userEntity.getPassword();
            return this.encoder.matches(loginAttempt.password(), storedPassword);
        }).switchIfEmpty(Mono.just(false));
    }

    @GetMapping( value = "/get", params = "id")
    public Mono<User> getByID(@RequestParam(required = false) String id) {
        Mono<UserEntity> monoEntity = this.service.findByID(id);
        return this.mapper.unmap(monoEntity).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping(value = "/get", params = "email")
    public Mono<User> getByEmail(@RequestParam(required = false) String email) {
        return this.mapper.unmap(this.service.findByEmail(email));
    }

    @PostMapping("/register")
    public Mono<Boolean> registerUser(@RequestBody UserRegister register) {
        UserEntity userEntity = new UserEntity("", register.nickname(), register.email(), "", this.DEFAULT_ICON);
        userEntity.setPassword(this.encoder.encode(register.password()));
        return this.service.register(userEntity);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Boolean> deleteByID(@PathVariable String id) {
        return this.service.deleteUserByID(id);
    }

    @GetMapping("/get")
    public Flux<User> findAll() {
        return this.mapper.mapFlux(this.service.findAll());
    }

    @PutMapping("/save")
    public Mono<Boolean> updateUser(@RequestBody User user) {
        return this.mapper.map(Mono.just(user)).flatMap(userEntity -> this.service.update(userEntity));
    }

    @GetMapping(value = "imageUrl", params = "id")
    public Mono<String> createImageUrl(@RequestParam String id) { // Este no va a funcionar hasta que este en un ECS
        return this.s3Service.generateUserIconUrl(id);
    }

    @PostMapping("/load")
    public Mono<String> load(@RequestBody UserEntity userEntity) {
        String password = userEntity.getPassword();
        userEntity.setPassword(this.encoder.encode(password)); // La hasheo aunque sea test para que el endpoint de login siga funcionando
        return this.service.register(userEntity).map(success ->  {
           if (success) {
               return "Se registro el usuario";
           } else {
               return "No se registro al usuario";
           }
        });
    }
}
