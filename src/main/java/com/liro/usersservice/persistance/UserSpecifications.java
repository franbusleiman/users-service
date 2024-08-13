package com.liro.usersservice.persistance;

import com.liro.usersservice.domain.model.User;
import com.liro.usersservice.domain.model.VetClient;
import com.liro.usersservice.domain.model.VetProfile;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

public class UserSpecifications {

    public static Specification<User> containsName(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> containsSurname(String surname) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("surname")), "%" + surname.toLowerCase() + "%");
    }

    public static Specification<User> hasDni(Long identificationNr) {
        return (root, query, cb) -> cb.equal(root.get("identificationNr"), identificationNr);
    }

    public static Specification<User> containsEmail(String email) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("email")),"%" + email.toLowerCase()+ "%");
    }

    public static Specification<User> hasVetId(Long vetId) {
        return (root, query, cb) -> {
            Join<User, VetClient> vetClientJoin = root.join("vetClients", JoinType.INNER);
            Join<VetClient, VetProfile> vetProfileJoin = vetClientJoin.join("vetProfile", JoinType.INNER);
            return cb.equal(vetProfileJoin.get("user").get("id"), vetId);
        };
    }
}