package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.RoleType;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "security_roles")
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;

    @ManyToMany(mappedBy = "targetRoles")
    private Set<News> news = new HashSet<>();

    public Role() {
    }

    public Role(RoleType name) {
        this.name = name;
    }

    public RoleType getName() {
        return name;
    }

    public void setName(RoleType name) {
        this.name = name;
    }

    public Set<News> getNews() {
        return news;
    }

    public Role setNews(Set<News> news) {
        this.news = news;
        return this;
    }
}
