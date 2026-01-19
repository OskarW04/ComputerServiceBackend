package com.example.ComputerService.controller;

import com.example.ComputerService.dto.request.EmployeeRequest;
import com.example.ComputerService.dto.response.EmployeeResponse;
import com.example.ComputerService.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('MANAGER', 'OFFICE', 'TECHNICIAN', 'WAREHOUSE')")
    public ResponseEntity<List<EmployeeResponse>> getAll(){
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/getAllTech")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<EmployeeResponse>> getAllTech(){
        return ResponseEntity.ok(employeeService.getAllTechnicians());
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable Long id){
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmployeeResponse> create(@RequestBody EmployeeRequest emp){
        return ResponseEntity.ok(employeeService.createEmployee(emp));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmployeeResponse> update(@PathVariable Long id,
                                                   @RequestBody EmployeeRequest emp){
        return ResponseEntity.ok(employeeService.updateEmployee(id, emp));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

}
