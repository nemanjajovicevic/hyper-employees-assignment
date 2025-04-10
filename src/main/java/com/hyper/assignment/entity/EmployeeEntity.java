package com.hyper.assignment.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "teams")
@ToString(exclude = "teams")
public class EmployeeEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id = UUID.randomUUID();

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "resource_id", unique = true, nullable = false)
    private UUID resourceId;

    @Column(name = "personal_id", unique = true, nullable = false)
    private Long personalId;

    @ManyToMany
    @JoinTable(
            name = "employee_team",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private Set<TeamEntity> teams = new HashSet<>();

    public EmployeeEntity(String name) {
        this.name = name;
    }

    public void addTeam(TeamEntity team) {
        if (!this.teams.contains(team)) {
            this.teams.add(team);
            team.getEmployees().add(this);
        }
    }

    public void removeTeam(TeamEntity team) {
        this.teams.remove(team);
        team.getEmployees().remove(this);
    }
}