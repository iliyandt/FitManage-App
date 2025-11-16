package demos.springdata.fitmanage.repository.support;

import demos.springdata.fitmanage.domain.dto.member.request.MemberFilter;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class MemberSpecification {
    public static Specification<User> build(MemberFilter filter) {
        List<Specification<User>> specs = new ArrayList<>();
        if (filter.getId() != null) {
            specs.add(id(filter.getId()));
        }
        if (filter.getFirstName() != null) {
            specs.add(firstName(filter.getFirstName()));
        }
        if (filter.getLastName() != null) {
            specs.add(lastName(filter.getLastName()));
        }
        if (filter.getGender() != null) {
            specs.add(gender(filter.getGender()));
        }
        if (filter.getEmployment() != null) {
            specs.add(employment(filter.getEmployment()));
        }
        if (filter.getBirthDate() != null) {
            specs.add(birthDate(filter.getBirthDate()));
        }
        if (filter.getEmail() != null) {
            specs.add(email(filter.getEmail()));
        }
        if (filter.getPhone() != null) {
            specs.add(phone(filter.getPhone()));
        }
        if (filter.getSubscriptionStatus() != null) {
            specs.add(subscriptionStatus(filter.getSubscriptionStatus()));
        }
        if (filter.getSubscriptionPlan() != null) {
            specs.add(subscriptionPlan(filter.getSubscriptionPlan()));
        }
        if (filter.getQrToken() != null) {
            specs.add(qrToken(filter.getQrToken()));
        }

        return specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());
    }

    private static Specification<User> id(Long id) {
        return (root, query, cb) ->
                cb.equal(root.get("id"), id);
    }

    private static Specification<User> firstName(String firstName) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    private static Specification<User> lastName(String lastName) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    private static Specification<User> gender(Gender gender) {
        return (root, query, cb) ->
                cb.equal(root.get("gender"), gender);
    }

    private static Specification<User> employment(Employment employment) {
        return (root, query, cb) ->
                cb.equal(root.join("memberships", JoinType.LEFT).get("employment"), employment);
    }

    private static Specification<User> birthDate(OffsetDateTime birthDate) {
        return (root, query, cb) ->
                cb.equal(root.get("birthDate"), birthDate);
    }

    private static Specification<User> email(String email) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private static Specification<User> phone(String phone) {
        return (root, query, cb) ->
                cb.like(root.get("phone"), "%" + phone + "%");
    }

    private static Specification<User> subscriptionStatus(SubscriptionStatus status) {
        return (root, query, cb) ->
                cb.equal(root.join("memberships", JoinType.LEFT).get("subscriptionStatus"), status);
    }

    private static Specification<User> subscriptionPlan(SubscriptionPlan plan) {
        return (root, query, cb) ->
                cb.equal(root.join("memberships", JoinType.LEFT).get("subscriptionPlan"), plan);
    }

    private static Specification<User> qrToken(String qrToken) {
        return (root, query, cb) ->
                cb.equal(root.get("qrToken"), qrToken);
    }

}
