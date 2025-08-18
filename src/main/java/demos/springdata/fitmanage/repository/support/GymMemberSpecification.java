package demos.springdata.fitmanage.repository.support;

import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberFilterRequestDto;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class GymMemberSpecification {
    public static Specification<Membership> build(GymMemberFilterRequestDto filter) {
        List<Specification<Membership>> specs = new ArrayList<>();

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

        return specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());
    }

    private static Specification<Membership> firstName(String firstName) {
        return (root, query, cb) -> firstName == null ? null :
                cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    private static Specification<Membership> lastName(String lastName) {
        return (root, query, cb) -> lastName == null ? null :
                cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    private static Specification<Membership> gender(Gender gender) {
        return (root, query, cb) -> gender == null ? null :
                cb.equal(root.get("gender"), gender);
    }

    private static Specification<Membership> employment(Employment employment) {
        return (root, query, cb) -> employment == null ? null :
                cb.equal(root.get("employment"), employment);
    }

    private static Specification<Membership> birthDate(OffsetDateTime birthDate) {
        return (root, query, cb) -> birthDate == null ? null :
                cb.equal(root.get("birthDate"), birthDate);
    }

    private static Specification<Membership> email(String email) {
        return (root, query, cb) -> email == null ? null :
                cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private static Specification<Membership> phone(String phone) {
        return (root, query, cb) -> phone == null ? null :
                cb.like(root.get("phone"), "%" + phone.toLowerCase() + "%");
    }

    private static Specification<Membership> subscriptionStatus(SubscriptionStatus status) {
        return (root, query, cb) -> status == null ? null :
                cb.equal(root.get("subscriptionStatus"), status);
    }

    private static Specification<Membership> subscriptionPlan(SubscriptionPlan plan) {
        return (root, query, cb) -> plan == null ? null :
                cb.equal(root.get("subscriptionPlan"), plan);
    }

}
