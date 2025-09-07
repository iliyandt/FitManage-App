package demos.springdata.fitmanage.domain.dto.employee;

public class EmployeeName {
    private Long id;
    private String name;

    public EmployeeName() {
    }

    public Long getId() {
        return id;
    }

    public EmployeeName setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public EmployeeName setName(String name) {
        this.name = name;
        return this;
    }
}
