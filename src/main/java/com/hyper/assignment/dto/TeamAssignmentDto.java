package com.hyper.assignment.dto;

import com.hyper.assignment.util.Team;

public record TeamAssignmentDto(
        Team team,
        boolean teamLead
) { }

