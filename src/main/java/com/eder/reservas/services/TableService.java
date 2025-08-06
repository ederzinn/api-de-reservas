package com.eder.reservas.services;

import com.eder.reservas.domain.table.Table;
import com.eder.reservas.dtos.table.TablePatchDTO;
import com.eder.reservas.dtos.table.TableRegisterDTO;
import com.eder.reservas.dtos.table.TableResponseDTO;
import com.eder.reservas.repositories.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TableService {
    @Autowired
    private TableRepository tableRepository;

    public List<TableResponseDTO> getAllTables() {
        return tableRepository.findAll().stream()
                .map(t -> new TableResponseDTO(
                        t.getId(),
                        t.getNumber(),
                        t.getCapacity(),
                        t.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public TableResponseDTO createTable(TableRegisterDTO data) {
        if(tableRepository.findByNumber(data.number()).isPresent()) {
            throw new RuntimeException("Table number already used");
        }
        Table newTable = new Table(data);
        tableRepository.save(newTable);

        return new TableResponseDTO(
                newTable.getId(),
                newTable.getNumber(),
                newTable.getCapacity(),
                newTable.getStatus()
        );
    }

    public TableResponseDTO patchTable(UUID id, TablePatchDTO data) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table does not exist"));

        data.number().ifPresent(n -> table.setNumber(n));
        data.capacity().ifPresent(c -> table.setCapacity(c));
        data.status().ifPresent(s -> table.setStatus(s));

        tableRepository.save(table);

        return new TableResponseDTO(
                table.getId(),
                table.getNumber(),
                table.getCapacity(),
                table.getStatus()
        );
    }

    public void deleteTable(UUID id) {
        if(!tableRepository.existsById(id)) {
            throw new RuntimeException("Table does not exist");
        }
        tableRepository.deleteById(id);
    }
}
