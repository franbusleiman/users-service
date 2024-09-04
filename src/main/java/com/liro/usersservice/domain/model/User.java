package com.liro.usersservice.domain.model;


import com.liro.usersservice.domain.enums.Gender;
import com.liro.usersservice.domain.model.Role;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
@EqualsAndHashCode(exclude = "addresses")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String identificationNr;
    private LocalDate birthDate;
    private Gender gender;
    private String email;
    private String name;
    private String surname;
    private String phoneNumber;
    private String areaPhoneNumber;

    @Column(name = "is_enabled")
    private boolean isEnabled;
    private int intents = 0;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "user")
    private VetProfile vetProfile;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Address> addresses;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles",
            joinColumns = {@JoinColumn(name = "users_id")},
            inverseJoinColumns = @JoinColumn(name = "roles_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<VetClient> vetClients;

}
