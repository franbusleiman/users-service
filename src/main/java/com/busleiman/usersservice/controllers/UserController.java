package com.busleiman.usersservice.controllers;

import com.busleiman.usersservice.domain.User;
import com.busleiman.usersservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {


    @Autowired
    private UserService userService;

     Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) throws InterruptedException {


        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) throws InterruptedException {

        logger.info("Getting user by username");

        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getUsers(){

        return ResponseEntity.ok(userService.findAll());
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody User user){

        User user1 = userService.createUser(user);

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
