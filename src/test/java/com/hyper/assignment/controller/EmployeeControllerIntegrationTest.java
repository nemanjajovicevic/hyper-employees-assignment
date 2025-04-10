package com.hyper.assignment.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyper.assignment.dto.ApiResponse;
import com.hyper.assignment.dto.EmployeeCreateDto;
import com.hyper.assignment.dto.EmployeeResponseDto;
import com.hyper.assignment.entity.EmployeeEntity;
import com.hyper.assignment.entity.TeamEntity;
import com.hyper.assignment.repository.EmployeeRepository;
import com.hyper.assignment.repository.TeamRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.UUID;

import static com.hyper.assignment.mock.MockEmployee.*;
import static com.hyper.assignment.util.Status.ERROR;
import static com.hyper.assignment.util.Status.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withInitScript("init.sql");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TeamRepository teamRepository;

    private MockMvc mockMvc;
    private static final String BASE_PATH = "/api/employees";
    private static final UUID employee1ResourceId = UUID.randomUUID();
    private static final UUID employee2ResourceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        TeamEntity developmentTeam = new TeamEntity("Development");
        TeamEntity qaTeam = new TeamEntity("QA");
        teamRepository.saveAll(Arrays.asList(developmentTeam, qaTeam));

        EmployeeEntity employee1 = new EmployeeEntity("Mirko");
        employee1.setResourceId(employee1ResourceId);
        employee1.setPersonalId(123456789L);
        employee1.addTeam(developmentTeam);
        employee1.addTeam(qaTeam);
        developmentTeam.setTeamLead(employee1);

        EmployeeEntity employee2 = new EmployeeEntity("Slavko");
        employee2.setResourceId(employee2ResourceId);
        employee2.setPersonalId(987654321L);
        employee2.addTeam(developmentTeam);
        employee2.addTeam(qaTeam);

        employeeRepository.saveAll(Arrays.asList(employee1, employee2));
    }

    @AfterEach
    void cleanUp() {
        employeeRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @Test
    void testGetEmployeeById() throws Exception {

        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + employee1ResourceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ApiResponse<EmployeeResponseDto> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<EmployeeResponseDto>>() {}
        );

        assertThat(response.status()).isEqualTo(SUCCESS);
        assertNotNull(response.data());

        EmployeeResponseDto employeeDto = response.data();
        assertThat(employeeDto.name()).isEqualTo("Mirko");
    }

    @Test
    void testGetEmployeeById_throwEmployeeNotFoundException() throws Exception {
        UUID nonExistingId = UUID.randomUUID();

        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        ApiResponse<?> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<Object>>() {} );

        assertThat(response.status()).isEqualTo(ERROR);
        assertThat(response.error()).isNotNull();
        assertThat(response.error().message()).contains("not found");
    }

    @Test
    void testCreateEmployee() throws Exception {
        EmployeeCreateDto employeeCreateDto = mockEmployeeCreateDto("Mirko");
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.post(BASE_PATH)
                                .content(objectMapper.writeValueAsString(employeeCreateDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ApiResponse<EmployeeResponseDto> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<EmployeeResponseDto>>() {} );

        assertThat(response.status()).isEqualTo(SUCCESS);
        assertNotNull(response.data());
        assertThat(response.data().name()).isEqualTo("Mirko");
    }

    @Test
    void testUpdateEmployee() throws Exception {
        EmployeeCreateDto updateDto = mockEmployeeCreateDto("Marko");

        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.put(BASE_PATH + "/" + employee1ResourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ApiResponse<EmployeeResponseDto> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<EmployeeResponseDto>>() {}
        );

        assertThat(response.status()).isEqualTo(SUCCESS);
        assertNotNull(response.data());
        assertThat(response.data().name()).isEqualTo("Marko");

        EmployeeEntity updatedEmployee = employeeRepository.findByResourceId(employee1ResourceId).orElseThrow();
        assertThat(updatedEmployee.getName()).isEqualTo("Marko");
    }

    @Test
    void testDeleteEmployee() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.delete(BASE_PATH + "/" + employee1ResourceId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();

        assertFalse(employeeRepository.existsByResourceId(employee1ResourceId));
    }
}
