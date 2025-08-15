package com.eder.reservas.controllers;

import com.eder.reservas.domain.table.TableStatus;
import com.eder.reservas.dtos.table.TablePatchDTO;
import com.eder.reservas.dtos.table.TableRegisterDTO;
import com.eder.reservas.dtos.table.TableResponseDTO;
import com.eder.reservas.exceptions.ApiException;
import com.eder.reservas.infra.security.SecurityConfig;
import com.eder.reservas.infra.security.TokenService;
import com.eder.reservas.repositories.UserRepository;
import com.eder.reservas.services.TableService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebMvcTest(TableController.class)
@WithMockUser(roles = "ADMIN")
@Import(SecurityConfig.class)
public class TableControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TableService tableService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TokenService tokenService;

    @Test
    public void shouldGetAllTablesAndReturn200() throws Exception {
        List<TableResponseDTO> tables = List.of(new TableResponseDTO(
                UUID.randomUUID(),
                1,
                2,
                TableStatus.AVAILABLE
        ));

        when(tableService.getAllTables()).thenReturn(tables);

        mockMvc.perform(get("/tables"))
                .andExpect(jsonPath("$[0].number").value(1))
                .andExpect(jsonPath("$[0].capacity").value(2))
                .andExpect(jsonPath("$[0].status").value(TableStatus.AVAILABLE.name()));
    }

    @Test
    public void shouldCreateTableAndReturn201() throws Exception {
        TableRegisterDTO data = new TableRegisterDTO(
                1,
                2,
                TableStatus.AVAILABLE
        );

        TableResponseDTO response = new TableResponseDTO(
                UUID.randomUUID(),
                1,
                2,
                TableStatus.AVAILABLE
        );

        when(tableService.createTable(any(TableRegisterDTO.class))).thenReturn(response);

        mockMvc.perform(post("/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.capacity").value(2))
                .andExpect(jsonPath("$.status").value(TableStatus.AVAILABLE.name()));
    }

    @Test
    public void shouldNotCreateTableAndThrow409() throws Exception {
        TableRegisterDTO data = new TableRegisterDTO(
                1,
                2,
                TableStatus.AVAILABLE
        );

        ApiException exception = new ApiException("Table number already used", HttpStatus.CONFLICT);

        when(tableService.createTable(any(TableRegisterDTO.class))).thenThrow(exception);

        mockMvc.perform(post("/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Table number already used"));
    }

    @Test
    public void shouldPatchTableAndReturn200() throws Exception {
        UUID id = UUID.randomUUID();

        TablePatchDTO data = new TablePatchDTO(
                null,
                Optional.of(4),
                null
        );

        TableResponseDTO response = new TableResponseDTO(
                id,
                1,
                4,
                TableStatus.AVAILABLE
        );

        when(tableService.patchTable(any(UUID.class), any(TablePatchDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/tables/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.capacity").value(4))
                .andExpect(jsonPath("$.status").value(TableStatus.AVAILABLE.name()));
    }

    @Test
    public void shouldNotPatchTableAndThrow404() throws Exception {
        UUID id = UUID.randomUUID();

        TablePatchDTO data = new TablePatchDTO(
                null,
                Optional.of(4),
                null
        );

        ApiException exception = new ApiException("Table does not exist", HttpStatus.NOT_FOUND);

        when(tableService.patchTable(any(UUID.class), any(TablePatchDTO.class))).thenThrow(exception);

        mockMvc.perform(patch("/tables/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Table does not exist"));
    }

    @Test
    public void shouldDeleteTableAndReturn204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/tables/{id}", id))
                .andExpect(status().isNoContent());

        verify(tableService, times(1)).deleteTable(any(UUID.class));
    }

    @Test
    public void shouldNotDeleteTableAndThrow404() throws Exception {
        UUID id = UUID.randomUUID();

        ApiException exception = new ApiException("Table does not exist", HttpStatus.NOT_FOUND);

        doThrow(exception).when(tableService).deleteTable(any(UUID.class));

        mockMvc.perform(delete("/tables/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Table does not exist"));
    }
}
