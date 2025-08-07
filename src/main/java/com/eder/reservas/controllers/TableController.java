package com.eder.reservas.controllers;

import com.eder.reservas.dtos.table.TableResponseDTO;
import com.eder.reservas.services.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tables")
public class TableController {
    @Autowired
    private TableService tableService;

    @GetMapping
    public ResponseEntity<List<TableResponseDTO>> getAllTables() {
        return null;
    }
}
