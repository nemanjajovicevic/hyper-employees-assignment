package com.hyper.assignment.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Error {

    EMPLOYEE_NOT_FOUND_ERROR_1100(1100, "Employee not found error"),
    TEAM_NOT_FOUND_ERROR_1101(1101, "Team not found error"),
    EMPLOYEE_VALIDATION_ERROR_1200(1200, "Employee validation error");

    private final int code;
    private final String message;

}