package com.liro.usersservice.controllers;

import com.liro.usersservice.domain.dtos.users.*;
import com.liro.usersservice.domain.model.User;
import com.liro.usersservice.services.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.util.List;

import static com.liro.usersservice.services.Util.getUser;

@RequestMapping("/users")
@RestController
public class UserController {


    @Autowired
    private UserService userService;

     Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserCompleteResponse> getUserById(@PathVariable("id") Long id) throws InterruptedException {

        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping(value = "/identificationNr/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> getUserByIdentificationNr(@PathVariable("id") String id) throws InterruptedException {

        return ResponseEntity.ok(userService.findByIdentificationNr(id));
    }


    @GetMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserCompleteResponse> getUserByUsername(@PathVariable("username") String username) throws InterruptedException {

        logger.info("Getting user by username");

        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @GetMapping(value = "/getCurrent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserCompleteResponse> getCurrentUser( @RequestHeader(name = "Authorization",  required = false) String token) throws InterruptedException {

        logger.info("Getting user by username");

        return ResponseEntity.ok(userService.findById(getUser(token).getId()));
    }

    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable("email") String email)  {

        logger.info("Getting user by username");

        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @GetMapping(value = "/existsByEmail/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> existsByEmail(@PathVariable("email") String email)  {


        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    @GetMapping(value = "/existsByIdentificationNr/{identificationNr}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> existsByIdentificationNr(@PathVariable("identificationNr") String identificationNr)  {


        return ResponseEntity.ok(userService.existsByIdentificationNr(identificationNr));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> createUser( @RequestBody @Valid  UserRegister userRegister){

        UserResponse user1 = userService.createUser(userRegister);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user1.getId())
                .toUri();

        return ResponseEntity.created(location).body(user1);
    }

    @PostMapping(value = "/client",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> createUserByVet(@RequestBody @Valid ClientRegister clientRegister,
                                                        @RequestHeader(name = "Authorization", required = false) String token){
        UserResponse userResponse = userService.createUserByVet(clientRegister, token);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(userResponse);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> changeState(@RequestBody User user,
                                            @PathVariable("id") Long id){

       return ResponseEntity.ok(userService.changeUserState(user, id));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id){

        userService.deleteUser(id);

        return ResponseEntity.ok().build();
    }

    @ApiPageable
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<UserAnimalsResponse>> getUserBySearchCriteria(Pageable pageable,
                                                                             @RequestParam("param") String param,
                                                                             @RequestHeader(name = "Authorization", required = false) String token) {


         return ResponseEntity.ok(userService.findAll(pageable, param, getUser(token)));
    }

    @Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query", value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query", value = "Number of records per page."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query", value = "Sorting criteria in the format: property(,asc|desc). "
                    + "Default sort order is ascending. " + "Multiple sort criteria are supported.") })
    @interface ApiPageable {
    }
}
