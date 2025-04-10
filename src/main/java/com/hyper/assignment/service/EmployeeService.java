package com.hyper.assignment.service;

import java.util.*;

import com.hyper.assignment.dto.EmployeeCreateDto;
import com.hyper.assignment.dto.EmployeeResponseDto;
import com.hyper.assignment.dto.TeamAssignmentDto;
import com.hyper.assignment.entity.EmployeeEntity;
import com.hyper.assignment.entity.TeamEntity;
import com.hyper.assignment.exception.EmployeeNotFoundException;
import com.hyper.assignment.exception.TeamNotFoundException;
import com.hyper.assignment.mapper.EmployeeMapper;
import com.hyper.assignment.repository.EmployeeRepository;
import com.hyper.assignment.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final String EMPLOYEE_NOT_FOUND = "Employee not found with resourceId: ";

    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;
    private final EmployeeMapper employeeMapper;

    public Optional<EmployeeResponseDto> getEmployeeByResourceId(UUID employeeResourceId) {
        return employeeRepository.findByResourceId(employeeResourceId)
                .map(employeeMapper::toResponseDto);
    }

    @Transactional
    public List<EmployeeEntity> searchEmployees(UUID resourceId, String name) {
        if (resourceId != null && StringUtils.hasText(name)) {
            return employeeRepository.findByResourceIdAndNameContainingIgnoreCase(resourceId, name);
        }
        if (resourceId != null) {
            return employeeRepository.findByResourceId(resourceId).stream()
                    .filter(employee -> employee.getResourceId().equals(resourceId))
                    .toList();
        }
        if (StringUtils.hasText(name)) {
            return employeeRepository.findByNameContainingIgnoreCase(name);
        }
        return employeeRepository.findAll();
    }

    @Transactional
    public EmployeeResponseDto createEmployee(EmployeeCreateDto employeeCreateDto) {
        EmployeeEntity employee = employeeMapper.fromCreateDto(employeeCreateDto);
        employee.setResourceId(UUID.randomUUID());
        employee.setPersonalId(new Random().nextLong());

        List<TeamEntity> teamsNeedingTeamLead = new ArrayList<>();

        for (TeamAssignmentDto assignment : employeeCreateDto.teamAssignments()) {
            String teamName = assignment.team().getName();
            TeamEntity team = teamRepository.findByName(teamName)
                    .orElseThrow(() -> new TeamNotFoundException("Team not found: " + teamName));

            employee.addTeam(team);

            if (assignment.teamLead()) {
                teamsNeedingTeamLead.add(team);
            }
        }

        EmployeeEntity savedEmployee = employeeRepository.save(employee);

        for (TeamEntity team : teamsNeedingTeamLead) {
            team.setTeamLead(savedEmployee);
            teamRepository.save(team);
        }

        return employeeMapper.toResponseDto(savedEmployee);
    }

    @Transactional
    public EmployeeResponseDto updateEmployee(UUID employeeResourceId, EmployeeCreateDto employeeUpdateDto) {
        EmployeeEntity employee = employeeRepository.findByResourceId(employeeResourceId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeResourceId));

        employee.setName(employeeUpdateDto.name());

        employee.getTeams().forEach(team -> team.getEmployees().remove(employee));
        employee.getTeams().clear();

        List<TeamEntity> teamsNeedingTeamLead = new ArrayList<>();

        if (employeeUpdateDto.teamAssignments() != null) {
            for (TeamAssignmentDto assignment : employeeUpdateDto.teamAssignments()) {
                String teamName = assignment.team().getName();
                TeamEntity team = teamRepository.findByName(teamName)
                        .orElseThrow(() -> new TeamNotFoundException("Team not found: " + teamName));

                employee.addTeam(team);

                if (assignment.teamLead()) {
                    teamsNeedingTeamLead.add(team);
                }
            }
        }

        EmployeeEntity updatedEmployee = employeeRepository.save(employee);
        updatedEmployee.getTeams().size();

        for (TeamEntity team : teamsNeedingTeamLead) {
            team.setTeamLead(updatedEmployee);
            teamRepository.save(team);
        }

        return employeeMapper.toResponseDto(updatedEmployee);
    }


    public void deleteEmployee(UUID employeeResourceId) {
        EmployeeEntity employee = employeeRepository.findByResourceId(employeeResourceId)
                .orElseThrow(
                        () -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND + employeeResourceId)
                );
        employeeRepository.delete(employee);
    }
}
