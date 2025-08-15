package com.eder.reservas.controllers;

import com.eder.reservas.dtos.table.TablePatchDTO;
import com.eder.reservas.dtos.table.TableRegisterDTO;
import com.eder.reservas.dtos.table.TableResponseDTO;
import com.eder.reservas.services.TableService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tables")
public class TableController {
    @Autowired
    private TableService tableService;

    @Transactional
    @GetMapping
    public ResponseEntity<List<TableResponseDTO>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @Transactional
    @PostMapping
    public ResponseEntity<TableResponseDTO> createTable(@Valid @RequestBody TableRegisterDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.createTable(data));
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<TableResponseDTO> patchTable(@PathVariable UUID id, @Valid @RequestBody TablePatchDTO data) {
        return ResponseEntity.ok(tableService.patchTable(id, data));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable UUID id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}
