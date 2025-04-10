package com.hyper.assignment.controller;

import com.hyper.assignment.dto.ApiResponse;
import com.hyper.assignment.dto.EmployeeCreateDto;
import com.hyper.assignment.dto.EmployeeResponseDto;
import com.hyper.assignment.entity.EmployeeEntity;
import com.hyper.assignment.exception.EmployeeNotFoundException;
import com.hyper.assignment.mapper.EmployeeMapper;
import com.hyper.assignment.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.hyper.assignment.util.Status.SUCCESS;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> getEmployeeById(@PathVariable @NotNull UUID id) {
        log.info("Received request to get employee by id: {}", id);
        return employeeService.getEmployeeByResourceId(id)
                .map(employee -> ResponseEntity.ok(employeeMapper.toResponseDto(employee, SUCCESS)))
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDto>>> searchEmployees(
            @RequestParam(required = false) UUID resourceId,
            @RequestParam(required = false) String name) {
        log.info("Received search request with resourceId: {} and name: {}", resourceId, name);
        List<EmployeeEntity> employees = employeeService.searchEmployees(resourceId, name);
        return ResponseEntity.ok(employeeMapper.toEmployeeListResponse(employees, SUCCESS));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> createEmployee(@Valid @RequestBody EmployeeCreateDto employee) {
        log.info("Received request to create employee: {}", employee);
        EmployeeResponseDto createdEmployee = employeeService.createEmployee(employee);
        ApiResponse<EmployeeResponseDto> response = employeeMapper.toResponseDto(createdEmployee, SUCCESS);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> updateEmployee(
            @PathVariable @NotNull UUID id,
            @Valid @RequestBody EmployeeCreateDto employee) {
        EmployeeResponseDto updatedEmployee = employeeService.updateEmployee(id, employee);
        ApiResponse<EmployeeResponseDto> response = employeeMapper.toResponseDto(updatedEmployee, SUCCESS);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable @NotNull UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
