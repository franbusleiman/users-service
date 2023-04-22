package com.busleiman.usersservice.controllers;

import com.busleiman.usersservice.domain.User;
import com.busleiman.usersservice.domain.dtos.UserDTO;
import com.busleiman.usersservice.domain.dtos.UserRegister;
import com.busleiman.usersservice.domain.dtos.UserResponse;
import com.busleiman.usersservice.services.UserService;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {


    @Autowired
    private UserService userService;

     Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id) throws InterruptedException {

        if (id == 5L) {
            Thread.sleep(2000);
        }
        else if (id ==6L){
            throw new InterruptedException();
        }

        else if (id ==7L){
            throw new com.busleiman.usersservice.exceptions.NotFoundException("usuario no encontrado");
        }

        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) throws InterruptedException {

        logger.info("Getting user by username");

        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserResponse>> getUsers(){

        return ResponseEntity.ok(userService.findAll());
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam("latitude") Long latitude, @RequestParam("longitude") Long longitude,
                                                       @RequestParam("kilometers") Long kilometers){

        return ResponseEntity.ok(userService.getUsersByLatLongAndDistance(latitude, longitude, kilometers));
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

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> changeState(@RequestBody User user,
                                            @PathVariable("id") Long id){

       return ResponseEntity.ok(userService.changeUserState(user, id));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id){

        userService.deleteUser(id);

        return ResponseEntity.status(204).build();
    }
}
