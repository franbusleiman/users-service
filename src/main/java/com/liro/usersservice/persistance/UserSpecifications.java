package com.liro.usersservice.persistance;

import com.liro.usersservice.domain.model.User;
import org.springframework.data.jpa.domain.Specification;
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

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("email")), email.toLowerCase());
    }

    public static Specification<User> hasVetId(Long vetId) {
        return (root, query, cb) -> cb.equal(root.get("vetId"), vetId);
    }
}