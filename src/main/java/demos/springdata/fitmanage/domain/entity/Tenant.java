package demos.springdata.fitmanage.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tenants")
public class Tenant extends BaseEntity {

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String businessEmail;

    private String address;

    private String city;

    @Column(name = "subscription_valid_until")
    private LocalDate subscriptionValidUntil;

    @OneToMany(mappedBy = "tenant", fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", fetch = FetchType.EAGER)
    private Set<Membership> memberships = new HashSet<>();


    public Tenant() {
    }

    public String getName() {
        return name;
    }


    public Tenant setName(String name) {
        this.name = name;
        return this;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public Tenant setBusinessEmail(String email) {
        this.businessEmail = email;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Tenant setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Tenant setCity(String city) {
        this.city = city;
        return this;
    }

    public LocalDate getSubscriptionValidUntil() {
        return subscriptionValidUntil;
    }

    public Tenant setSubscriptionValidUntil(LocalDate subscriptionValidUntil) {
        this.subscriptionValidUntil = subscriptionValidUntil;
        return this;
    }

    public List<User> getUsers() {
        return users;
    }

    public Tenant setUsers(List<User> users) {
        this.users = users;
        return this;
    }

    public Set<Membership> getMemberships() {
        return memberships;
    }

    public Tenant setMemberships(Set<Membership> memberships) {
        this.memberships = memberships;
        return this;
    }
}
