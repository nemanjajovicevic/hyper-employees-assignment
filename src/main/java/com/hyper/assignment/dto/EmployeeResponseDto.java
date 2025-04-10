package com.hyper.assignment.dto;

import java.util.Set;
import java.util.UUID;

public record EmployeeResponseDto(
        UUID id,
        String name,
        Set<TeamAssignmentDto> teamAssignments
) { }
