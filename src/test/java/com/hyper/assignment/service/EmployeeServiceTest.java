package com.hyper.assignment.service;

import com.hyper.assignment.dto.EmployeeCreateDto;
import com.hyper.assignment.dto.EmployeeResponseDto;
import com.hyper.assignment.dto.TeamAssignmentDto;
import com.hyper.assignment.entity.EmployeeEntity;
import com.hyper.assignment.entity.TeamEntity;
import com.hyper.assignment.exception.EmployeeNotFoundException;
import com.hyper.assignment.mapper.EmployeeMapper;
import com.hyper.assignment.repository.EmployeeRepository;
import com.hyper.assignment.repository.TeamRepository;
import com.hyper.assignment.util.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.hyper.assignment.mock.MockEmployee.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private UUID employeeResourceId;

    @BeforeEach
    void setUp() {
        employeeResourceId = UUID.randomUUID();
    }

    @Test
    void testGetEmployeeByResourceId() {
        UUID resourceId = UUID.randomUUID();
        EmployeeEntity entity = mockEmployeeEntity(resourceId, "Mirko");
        EmployeeResponseDto responseDto = mockEmployeeResponseDto(resourceId);

        when(employeeRepository.findByResourceId(resourceId)).thenReturn(Optional.of(entity));
        when(employeeMapper.toResponseDto(entity)).thenReturn(responseDto);

        Optional<EmployeeResponseDto> result = employeeService.getEmployeeByResourceId(resourceId);

        assertThat(result).isPresent().contains(responseDto);
        verify(employeeRepository).findByResourceId(resourceId);
        verify(employeeMapper).toResponseDto(entity);
    }

    @Test
    void testGetEmployeeByResourceId_NotFound() {
        UUID resourceId = UUID.randomUUID();

        when(employeeRepository.findByResourceId(resourceId)).thenReturn(Optional.empty());

        Optional<EmployeeResponseDto> result = employeeService.getEmployeeByResourceId(resourceId);

        assertThat(result).isNotPresent();
        verify(employeeRepository).findByResourceId(resourceId);
        verify(employeeMapper, never()).toResponseDto(any());
    }

    @Test
    void testCreateEmployee() {
        EmployeeCreateDto createDto = new EmployeeCreateDto(
                "Test Employee",
                Set.of(new TeamAssignmentDto(Team.DEVELOPMENT, true))
        );

        EmployeeEntity employee = new EmployeeEntity("Test Employee");
        when(employeeMapper.fromCreateDto(createDto)).thenReturn(employee);

        TeamEntity developmentTeam = new TeamEntity("Development");
        when(teamRepository.findByName("Development")).thenReturn(Optional.of(developmentTeam));

        when(employeeRepository.save(employee)).thenAnswer(invocation -> invocation.getArgument(0));

        when(teamRepository.save(developmentTeam)).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponseDto expectedDto = new EmployeeResponseDto(
                employee.getResourceId(),
                "Test Employee",
                employee.getTeams().stream()
                        .map(t -> new TeamAssignmentDto(
                                Team.valueOf(t.getName().toUpperCase()),
                                t.getTeamLead() != null))
                        .collect(Collectors.toSet())
        );
        when(employeeMapper.toResponseDto(employee)).thenReturn(expectedDto);

        EmployeeResponseDto result = employeeService.createEmployee(createDto);

        assertEquals(1, employee.getTeams().size(), "Employee should have one assigned team");
        TeamEntity assignedTeam = employee.getTeams().iterator().next();
        assertEquals("Development", assignedTeam.getName(), "Assigned team should be 'Development'");

        assertEquals(employee, developmentTeam.getTeamLead(), "The team lead should be the newly saved employee");

        verify(employeeMapper).fromCreateDto(createDto);
        verify(teamRepository).findByName("Development");
        verify(employeeRepository).save(employee);
        verify(teamRepository).save(developmentTeam);
        verify(employeeMapper).toResponseDto(employee);

        assertEquals(expectedDto, result, "Resulting response DTO should match the expected DTO");
    }

    @Test
    void testUpdateEmployee() {
        EmployeeEntity employee = new EmployeeEntity("Old Employee");
        employee.setResourceId(employeeResourceId);

        TeamEntity oldTeam = new TeamEntity("QA");
        employee.addTeam(oldTeam);

        TeamAssignmentDto assignment = new TeamAssignmentDto(Team.DEVELOPMENT, true);
        Set<TeamAssignmentDto> assignments = new HashSet<>();
        assignments.add(assignment);
        EmployeeCreateDto updateDto = new EmployeeCreateDto("Updated Employee", assignments);

        TeamEntity developmentTeam = new TeamEntity("Development");

        when(employeeRepository.findByResourceId(employeeResourceId))
                .thenReturn(Optional.of(employee));
        when(teamRepository.findByName("Development"))
                .thenReturn(Optional.of(developmentTeam));
        when(employeeRepository.save(employee))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(teamRepository.save(developmentTeam))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponseDto expectedResponseDto = new EmployeeResponseDto(
                employee.getResourceId(),
                "Updated Employee",
                employee.getTeams().stream()
                        .map(t -> new TeamAssignmentDto(
                                Team.valueOf(t.getName().toUpperCase()),
                                t.getTeamLead() != null))
                        .collect(Collectors.toSet())
        );
        when(employeeMapper.toResponseDto(employee)).thenReturn(expectedResponseDto);

        EmployeeResponseDto result = employeeService.updateEmployee(employeeResourceId, updateDto);

        assertEquals("Updated Employee", employee.getName());
        assertEquals(1, employee.getTeams().size(), "Employee should have one team after update");
        TeamEntity assignedTeam = employee.getTeams().iterator().next();
        assertEquals("Development", assignedTeam.getName());
        assertEquals(employee, developmentTeam.getTeamLead(), "Team lead should be set to the updated employee");

        verify(employeeRepository).findByResourceId(employeeResourceId);
        verify(teamRepository).findByName("Development");
        verify(employeeRepository).save(employee);
        verify(teamRepository).save(developmentTeam);
        verify(employeeMapper).toResponseDto(employee);

        assertEquals(expectedResponseDto, result, "Returned response DTO should match expected");
    }

    @Test
    void testUpdateEmployee_NotFound() {
        UUID resourceId = UUID.randomUUID();
        EmployeeCreateDto updateDto = mock(EmployeeCreateDto.class);

        when(employeeRepository.findByResourceId(resourceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployee(resourceId, updateDto))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("Employee not found: " + resourceId);

        verify(employeeRepository).findByResourceId(resourceId);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testDeleteEmployee() {
        UUID resourceId = UUID.randomUUID();
        EmployeeEntity entity = new EmployeeEntity();

        when(employeeRepository.findByResourceId(resourceId)).thenReturn(Optional.of(entity));

        employeeService.deleteEmployee(resourceId);

        verify(employeeRepository).findByResourceId(resourceId);
        verify(employeeRepository).delete(entity);
    }

    @Test
    void testDeleteEmployee_NotFound() {
        UUID resourceId = UUID.randomUUID();

        when(employeeRepository.findByResourceId(resourceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.deleteEmployee(resourceId))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("Employee not found with resourceId: " + resourceId);

        verify(employeeRepository).findByResourceId(resourceId);
        verify(employeeRepository, never()).delete(any());
    }
}