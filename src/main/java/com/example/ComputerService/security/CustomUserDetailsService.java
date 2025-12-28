package com.example.ComputerService.security;

import com.example.ComputerService.repository.ClientRepository;
import com.example.ComputerService.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository, ClientRepository clientRepository) {
        this.employeeRepository = employeeRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var employeeOpt = employeeRepository.findByEmail(email);
        if (employeeOpt.isPresent()) {
            return new SecurityUser(employeeOpt.get());
        }

        var clientOpt = clientRepository.findByEmail(email);
        if (clientOpt.isPresent()) {
            return new SecurityUser(clientOpt.get());
        }

        throw new UsernameNotFoundException("Can't find user with provided email: " + email);
    }
}
