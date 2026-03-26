package org.dam2.bomboplatsserver.controller;


import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.dam2.bomboplats.api.Direccion;
import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplats.api.User;
import org.dam2.bomboplats.api.login.LoginAttempt;
import org.dam2.bomboplats.api.login.UserRegister;
import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.dam2.bomboplatsserver.modelo.entity.PlatoFavoritosEntity;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.modelo.mapper.DireccionEntityMapper;
import org.dam2.bomboplatsserver.modelo.mapper.PlatoEntityMapper;
import org.dam2.bomboplatsserver.modelo.mapper.UserEntityMapper;
import org.dam2.bomboplatsserver.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired private IUserService service;
    @Autowired private IS3Service s3Service;

    @PutMapping("/login")
    @Operation(summary = "Comprueba el login de un usuario")
    @ApiResponse(responseCode = "200", description = "true si el login es correcto, false si el login es incorrecto o no se encuentra ese email")
    public Mono<Boolean> login(@RequestBody LoginAttempt loginAttempt) {
        return this.service.verifyLogin(loginAttempt);
    }

    @GetMapping( value = "/get", params = "id")
    @Operation(summary = "Obtener un usuario por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado al usuario"),
            @ApiResponse(responseCode = "500", description = "Los parámetros son incorrectos")
    })
    public Mono<User> getByID(@RequestParam(required = false) String id) {
        return this.service.findByID(id);
    }

    @GetMapping(value = "/get", params = "email")
    @Operation(summary = "Obtener un usuario por su Email")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado al usuario"),
            @ApiResponse(responseCode = "500", description = "Los parametros son incorrectos")
    })
    public Mono<User> getByEmail(@RequestParam(required = false) String email) {
        return this.service.findByEmail(email);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un usuario")
    @ApiResponse(responseCode = "200", description = "true: Usuario registrado. false: Usuario ya existía o hubo un error")
    public Mono<User> registerUser(@RequestBody UserRegister register) {
        return this.service.register(register);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar un usuario")
    @ApiResponse(responseCode = "200", description = "true: Usuario eliminado. false: Usuario no existía o hubo un error")
    public Mono<Boolean> deleteByID(@PathVariable String id) {
        return this.service.deleteUserByID(id);
    }

    @GetMapping("/get")
    @Operation(summary = "Obtener todos los usuarios")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Usuarios encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado usuarios")
    })
    public Flux<User> findAll() {
        return this.service.findAll();
    }

    @PutMapping("/save")
    @Operation(summary = "Actualizar información de un usuario")
    @ApiResponse(responseCode = "200", description = "true: Usuario actualizado. false: Usuario no existía o hubo un error")
    public Mono<Boolean> updateUser(@RequestBody User user) {
        return this.service.update(user);
    }

    @GetMapping(value = "imageUrl", params = "id")
    @Operation(summary = "Generar link para subir fotos de usuarios al S3")
    @ApiResponse(responseCode = "200", description = "Se genero el link")
    public Mono<String> createImageUrl(@RequestParam String id) { // Este no va a funcionar hasta que esté en un ECS
        return this.s3Service.generateUserIconUrl(id);
    }

    @PutMapping(value = "/updatePass", params = {"userId", "password"})
    @Operation(summary = "Actualizar la contraseña de un usuario segun su id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "true: Se actualizo la contraseña correctamente"),
        @ApiResponse(responseCode = "404", description = "No se ha encontrado al usuario con ese ID")
    })
    public Mono<Boolean> updatePassword(@RequestParam String userId, @RequestParam String password) {
        return this.service.updatePassword(userId, password);
    }

    @GetMapping(value = "/  platosfavoritos", params = "id")
    @Operation(summary = "Obtener los platos favoritos de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Platos favoritos del usuario encontrados correctamente"),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado el usuario")
    })
    public Flux<Plato> getPlatosFavoritos(@RequestParam String id) {
        return this.service.getPlatosFavoritos(id);
    }

}
