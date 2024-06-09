package com.liro.usersservice.controllers;

import com.liro.usersservice.domain.dtos.address.AddressDTO;
import com.liro.usersservice.domain.dtos.address.AddressResponse;
import com.liro.usersservice.services.AddressService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RequestMapping("/addresses")
@RestController
public class AddressController {


    @Autowired
    private AddressService addressService;


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createAddress(@RequestBody @Valid AddressDTO addressDTO, @RequestHeader(name = "Authorization", required = false) String token) {

        System.out.println("entro");
        AddressResponse address = addressService.createAddress(addressDTO, getUserId(token));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(address.getId())
                .toUri();

        System.out.println("salio");

        return ResponseEntity.created(location).build();
    }

    private Long getUserId(String token){
        Claims claims;
        Long userId;

        claims = Jwts.parser()
                //     .setSigningKey("codigo_secreto".getBytes())
                .setSigningKey("asdfAEGVDSAkdnASBOIAW912927171Q23Q".getBytes())
                .parseClaimsJws(token.substring(7))
                .getBody();

        return Long.valueOf((Integer) claims.get("id"));
    }
}
