package com.hyper.assignment.dto;

import java.util.Set;

public record EmployeeCreateDto(
        String name,
        Set<TeamAssignmentDto> teamAssignments
) { }

