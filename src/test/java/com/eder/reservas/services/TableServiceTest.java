package com.eder.reservas.services;

import com.eder.reservas.domain.table.Table;
import com.eder.reservas.domain.table.TableStatus;
import com.eder.reservas.dtos.table.TablePatchDTO;
import com.eder.reservas.dtos.table.TableRegisterDTO;
import com.eder.reservas.dtos.table.TableResponseDTO;
import com.eder.reservas.exceptions.ApiException;
import com.eder.reservas.repositories.TableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TableServiceTest {
    @Mock
    private TableRepository tableRepository;
    @InjectMocks
    private TableService tableService;

    @Test
    public void shouldGetAllTables() {
        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(2);
        table.setStatus(TableStatus.AVAILABLE);
        List<Table> tables = List.of(table);

        when(tableRepository.findAll()).thenReturn(tables);

        List<TableResponseDTO> response = tableService.getAllTables();

        assertNotNull(response);
        assertEquals(1, response.get(0).number());
        assertEquals(2, response.get(0).capacity());
        assertEquals(TableStatus.AVAILABLE, response.get(0).status());
        verify(tableRepository, times(1)).findAll();
    }

    @Test
    public void shouldCreateTableIfNumberIsAvailable() {
        Table existingTable = new Table();
        existingTable.setNumber(1);
        existingTable.setCapacity(2);
        existingTable.setStatus(TableStatus.AVAILABLE);

        when(tableRepository.findByNumber(anyInt())).thenReturn(Optional.empty());
        when(tableRepository.save(any(Table.class))).thenReturn(existingTable);

        TableResponseDTO response = tableService.createTable(new TableRegisterDTO(
                existingTable.getNumber(),
                existingTable.getCapacity(),
                existingTable.getStatus()
        ));

        assertNotNull(response);
        assertEquals(1, response.number());
        assertEquals(2, response.capacity());
        assertEquals(TableStatus.AVAILABLE, response.status());
        verify(tableRepository, times(1)).findByNumber(anyInt());
        verify(tableRepository, times(1)).save(any(Table.class));
    }

    @Test
    public void shouldNotCreateTableIfNumberIsNotAvailable() {
        Table existingTable = new Table();
        existingTable.setNumber(1);
        existingTable.setCapacity(2);
        existingTable.setStatus(TableStatus.AVAILABLE);

        when(tableRepository.findByNumber(anyInt())).thenReturn(Optional.of(existingTable));

        ApiException exception = assertThrows(ApiException.class, () -> {
            tableService.createTable(new TableRegisterDTO(
                    existingTable.getNumber(),
                    existingTable.getCapacity(),
                    existingTable.getStatus()
            ));
        });
        assertEquals("Table number already used", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getErrorStatus());
        verify(tableRepository, times(1)).findByNumber(anyInt());
        verify(tableRepository, never()).save(any(Table.class));
    }

    @Test
    public void shouldPatchTableIfExists() {
        UUID id = UUID.randomUUID();
        TablePatchDTO data = new TablePatchDTO(
                Optional.empty(),
                Optional.of(4),
                Optional.empty()
        );

        Table existingTable = new Table();
        existingTable.setNumber(1);
        existingTable.setCapacity(2);
        existingTable.setStatus(TableStatus.AVAILABLE);

        when(tableRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingTable));
        when(tableRepository.save(any(Table.class))).thenReturn(existingTable);

        TableResponseDTO response = tableService.patchTable(id, data);

        assertNotNull(response);
        assertEquals(4, response.capacity());
        assertEquals(1, response.number());
        assertEquals(TableStatus.AVAILABLE, response.status());
        verify(tableRepository, times(1)).findById(any(UUID.class));
        verify(tableRepository, times(1)).save(any(Table.class));
    }

    @Test
    public void shouldThrowErrorIfCanNotPatch() {
        UUID id = UUID.randomUUID();
        TablePatchDTO data = new TablePatchDTO(
                Optional.empty(),
                Optional.of(4),
                Optional.empty()
        );

        when(tableRepository.findById(id)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            tableService.patchTable(id, data);
        });
        assertEquals("Table does not exist", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getErrorStatus());
        verify(tableRepository,times(1)).findById(any(UUID.class));
        verify(tableRepository, never()).save(any(Table.class));
    }

    @Test
    public void shouldDeleteTableIfExists(){
        UUID id = UUID.randomUUID();

        when(tableRepository.existsById(any(UUID.class))).thenReturn(true);

        assertDoesNotThrow(() -> tableService.deleteTable(id));
        verify(tableRepository, times(1)).existsById(any(UUID.class));
        verify(tableRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void shouldThrowErrorIfCanNotDelete() {
        UUID id = UUID.randomUUID();

        when(tableRepository.existsById(any(UUID.class))).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class, () -> {
            tableService.deleteTable(id);
        });
        assertEquals("Table does not exist", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getErrorStatus());
        verify(tableRepository, times(1)).existsById(any(UUID.class));
        verify(tableRepository, never()).deleteById(any(UUID.class));
    }
}
