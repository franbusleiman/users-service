package com.liro.usersservice.persistance;

import com.liro.usersservice.domain.model.User;
import com.liro.usersservice.domain.model.VetProfile;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.List;

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

    public static Specification<User> hasIdIn(List<Long> ids) {
        return (root, query, criteriaBuilder) -> root.get("id").in(ids);
    }

    public static Specification<User> isEnabled(Boolean enabled){
        return (root, query, cb) -> cb.equal(root.get("isEnabled"), enabled);
    }
}