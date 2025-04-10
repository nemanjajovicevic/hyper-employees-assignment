package com.hyper.assignment.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "employees")
@ToString(exclude = "employees")
public class TeamEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id = UUID.randomUUID();

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "teams")
    private Set<EmployeeEntity> employees = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "team_lead_id")
    private EmployeeEntity teamLead;

    public TeamEntity(String name) {
        this.name = name;
    }
}
