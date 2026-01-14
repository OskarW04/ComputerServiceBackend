package com.example.ComputerService.config;

import com.example.ComputerService.model.Employee;
import com.example.ComputerService.model.enums.EmployeeRole;
import com.example.ComputerService.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetupFirstAdmin implements CommandLineRunner {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String ... args){
        if(employeeRepository.findByEmail("admin@serwis.pl").isEmpty()){
            employeeRepository.save(new Employee(null, "admin", "admin", "admin@serwis.pl", passwordEncoder.encode("admin123"), EmployeeRole.MANAGER, null));
            System.out.println("Created new admin account");
        }

    }
}
