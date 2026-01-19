package com.example.ComputerService.service;

import com.example.ComputerService.dto.request.EmployeeRequest;
import com.example.ComputerService.dto.response.EmployeeResponse;
import com.example.ComputerService.mapper.EmployeeMapper;
import com.example.ComputerService.model.Employee;
import com.example.ComputerService.model.enums.EmployeeRole;
import com.example.ComputerService.model.enums.SkillLevel;
import com.example.ComputerService.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeMapper employeeMapper;

    // get all employees as list
    public List<EmployeeResponse> getAllEmployees(){
        return employeeRepository.findAll().stream()
                .map(employeeMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    //get all technicians
    public List<EmployeeResponse> getAllTechnicians(){
        return employeeRepository.findAllByRole(EmployeeRole.TECHNICIAN).stream()
                .map(employeeMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    // get employee by ID
    public EmployeeResponse getEmployeeById(Long id){
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return employeeMapper.mapToResponse(emp);
    }

    // create new employee
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request){
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Employee with that email already exists");
        }
        if(request.getPassword() == null || request.getPassword().isEmpty()){
            throw new RuntimeException("Password has to be provided");
        }
        Employee emp = new Employee();
        emp.setEmail(request.getEmail());
        emp.setFirstName(request.getFirstName());
        emp.setLastName(request.getLastName());
        emp.setPassword(passwordEncoder.encode(request.getPassword()));
        emp.setRole(EmployeeRole.valueOf(request.getRole().toUpperCase()));
        if(request.getSkillLevel() != null && !request.getSkillLevel().isBlank()){
            emp.setSkillLevel(SkillLevel.valueOf(request.getSkillLevel().toUpperCase()));
        }
        else {
            emp.setSkillLevel(null);
        }
        Employee saved = employeeRepository.save(emp);
        return employeeMapper.mapToResponse(saved);
    }

    // update employee
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest edit){
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        emp.setFirstName(edit.getFirstName());
        emp.setLastName(edit.getLastName());
        emp.setEmail(edit.getEmail());
        emp.setRole(EmployeeRole.valueOf(edit.getRole().toUpperCase()));
        if(edit.getSkillLevel() != null && !edit.getSkillLevel().isBlank()){
            emp.setSkillLevel(SkillLevel.valueOf(edit.getSkillLevel().toUpperCase()));
        }
        else {
            emp.setSkillLevel(null);
        }
        if (edit.getPassword() != null && !edit.getPassword().isBlank()) {
            emp.setPassword(passwordEncoder.encode(edit.getPassword()));
        }
        Employee updated = employeeRepository.save(emp);
        return employeeMapper.mapToResponse(updated);
    }

    // delete employee
    public void deleteEmployee(Long id){
        if(!employeeRepository.existsById(id)){
            throw new RuntimeException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }


}
