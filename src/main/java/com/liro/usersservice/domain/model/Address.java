package com.liro.usersservice.domain.model;


import com.liro.usersservice.domain.validationGroups.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address")
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String addressLine1;
    private String addressLine2;
    @Column(nullable = false)
    private String city;
    private String location;
    @Column(nullable = false)
    private String country;
    private String postalCode;
    private Boolean mainAddress;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User  user;
}
