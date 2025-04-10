package com.hyper.assignment.mapper;

import com.hyper.assignment.dto.ApiResponse;
import com.hyper.assignment.dto.EmployeeCreateDto;
import com.hyper.assignment.dto.EmployeeResponseDto;
import com.hyper.assignment.entity.EmployeeEntity;
import com.hyper.assignment.util.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teams", ignore = true)
    EmployeeEntity fromCreateDto(EmployeeCreateDto dto);

    EmployeeResponseDto toResponseDto(EmployeeEntity employee);

    @Mapping(
        target = "timestamp",
        expression =
                "java(com.hyper.assignment.util.DateTimeUtil.getFormattedLocalDateTimeNow(com.hyper.assignment.util.DateTimeUtil.DATE_TIME_FORMAT))")
    default ApiResponse<EmployeeResponseDto> toResponseDto(EmployeeResponseDto employee, Status status) {
        return ApiResponse.<EmployeeResponseDto>builder()
                .status(status)
                .data(employee)
                .build();
    }

    @Mapping(
            target = "timestamp",
            expression =
                    "java(com.hyper.assignment.util.DateTimeUtil.getFormattedLocalDateTimeNow(com.hyper.assignment.util.DateTimeUtil.DATE_TIME_FORMAT))")
    default ApiResponse<List<EmployeeResponseDto>> toEmployeeListResponse(List<EmployeeEntity> employees, Status status) {
        List<EmployeeResponseDto> dtoList = employees.stream()
                .map(this::toResponseDto)
                .toList();
        return ApiResponse.<List<EmployeeResponseDto>>builder()
                .status(status)
                .data(dtoList)
                .build();
    }
}
