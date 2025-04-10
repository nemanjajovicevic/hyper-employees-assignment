package com.hyper.assignment.mock;

import com.hyper.assignment.dto.EmployeeCreateDto;
import com.hyper.assignment.dto.EmployeeResponseDto;
import com.hyper.assignment.dto.TeamAssignmentDto;
import com.hyper.assignment.entity.EmployeeEntity;

import java.util.Set;
import java.util.UUID;

import static com.hyper.assignment.util.Team.DEVELOPMENT;
import static com.hyper.assignment.util.Team.QA;

public class MockEmployee {

    public static EmployeeResponseDto mockEmployeeResponseDto(UUID resourceId) {
        return new EmployeeResponseDto(
                resourceId,
                "John Doe",
                Set.of(
                        new TeamAssignmentDto(DEVELOPMENT, true),
                        new TeamAssignmentDto(QA, false)
                )
        );
    }

    public static EmployeeEntity mockEmployeeEntity(UUID resourceId, String name) {
        EmployeeEntity employee = new EmployeeEntity(name);
        employee.setResourceId(resourceId);
        return employee;
    }

    public static EmployeeCreateDto mockEmployeeCreateDto(String name) {
        return new EmployeeCreateDto(
                name,
                Set.of(
                        new TeamAssignmentDto(DEVELOPMENT, true),
                        new TeamAssignmentDto(QA, false)
                )
        );
    }
}
