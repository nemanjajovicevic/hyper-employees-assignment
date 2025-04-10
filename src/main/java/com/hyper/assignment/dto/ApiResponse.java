package com.hyper.assignment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyper.assignment.util.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        @NotBlank String timestamp,
        @NotBlank Status status,
        T data,
        ErrorDto error
) {
    @Builder
    public record ErrorDto(Integer code, String message) { }
}
