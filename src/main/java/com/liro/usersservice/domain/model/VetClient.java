package com.liro.usersservice.domain.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "vet_client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VetClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST,
            optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.PERSIST,
            optional = false)
    @JoinColumn(name = "vet_profiles_id", nullable = false)
    private VetProfile vetProfile;

    private Long accountBalance;
}
