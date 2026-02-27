package org.dam2.bomboplatsserver.controller;


import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.dam2.bomboplats.api.User;
import org.dam2.bomboplats.api.login.LoginAttempt;
import org.dam2.bomboplats.api.login.UserRegister;
import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.modelo.mapper.DireccionEntityMapper;
import org.dam2.bomboplatsserver.modelo.mapper.UserEntityMapper;
import org.dam2.bomboplatsserver.service.IDireccionService;
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
    @Autowired private IDireccionService direccionService;
    @Autowired private UserEntityMapper mapper;
    @Autowired private DireccionEntityMapper direccionMapper;
    @Autowired private PasswordEncoder encoder;
    @Autowired private IS3Service s3Service;
    private final String DEFAULT_ICON = "profile/default.jpg"; // En el almacenamiento de imagenes, tendremos una por defecto y palante

    @PutMapping("/login")
    @Operation(summary = "Comprueba el login de un usuario")
    @ApiResponse(responseCode = "200", description = "true si el login es correcto, false si el login es incorrecto o no se encuentra ese email")
    public Mono<Boolean> login(@RequestBody LoginAttempt loginAttempt) {
        return this.service.findByEmail(loginAttempt.email()).map(userEntity -> {
            String storedPassword = userEntity.getPassword();
            return this.encoder.matches(loginAttempt.password(), storedPassword);
        }).switchIfEmpty(Mono.just(false));
    }

    @GetMapping( value = "/get", params = "id")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado al usuario"),
            @ApiResponse(responseCode = "500", description = "Los parámetros son incorrectos")
    })
    public Mono<User> getByID(@RequestParam(required = false) String id) {
        Mono<UserEntity> monoEntity = this.service.findByID(id);
        return this.mapper.unmap(monoEntity).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping(value = "/get", params = "email")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado al usuario"),
            @ApiResponse(responseCode = "500", description = "Los parametros son incorrectos")
    })
    public Mono<User> getByEmail(@RequestParam(required = false) String email) {
        return this.mapper.unmap(this.service.findByEmail(email))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un usuario")
    @ApiResponse(responseCode = "200", description = "true: Usuario registrado. false: Usuario ya existía o hubo un error")
    public Mono<Boolean> registerUser(@RequestBody UserRegister register) {
        UserEntity userEntity = new UserEntity("", register.nickname(), register.email(), "", this.DEFAULT_ICON);
        userEntity.setPassword(this.encoder.encode(register.password()));
        return this.service.register(userEntity);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar un usuario")
    @ApiResponse(responseCode = "200", description = "true: Usuario eliminado. false: Usuario no existía o hubo un error")
    public Mono<Boolean> deleteByID(@PathVariable String id) {
        return this.service.deleteUserByID(id);
    }

    @GetMapping("/get")
    @Operation(summary = "Obtener todos los usuarios, o por su id/email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado usuarios")
    })
    public Flux<User> findAll() {
        return this.mapper.mapFlux(this.service.findAll());
    }

    @PutMapping("/save")
    @Operation(summary = "Actualizar informacion de un usuario")
    @ApiResponse(responseCode = "200", description = "true: Usuario actualizado. false: Usuario no existía o hubo un error")
    public Mono<Boolean> updateUser(@RequestBody User user) {

        return this.mapper.map(Mono.just(user)).flatMap(userEntity -> {
            user.getDirecciones().forEach(direccion -> {
                this.direccionMapper.map(Mono.just(direccion)) // Esto está mal y para corregirlo es una puta mierda, ya lo haré
                        .doOnNext(direccionEntity -> {
                            direccionEntity.setIdUser(user.getId());
                            this.direccionService.update(direccionEntity);
                        });
            });
            return this.service.update(userEntity);
        });
    }

    @GetMapping(value = "imageUrl", params = "id")
    @Operation(summary = "Generar link para subir fotos de usuarios al S3")
    @ApiResponse(responseCode = "200", description = "Se genero el link")
    public Mono<String> createImageUrl(@RequestParam String id) { // Este no va a funcionar hasta que esté en un ECS
        return this.s3Service.generateUserIconUrl(id);
    }

    // FUNCION UNICAMENTE PARA TESTEO
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
