package com.example.ComputerService.security;
import com.example.ComputerService.model.Client;
import com.example.ComputerService.model.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
public class SecurityUser implements UserDetails{
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    //Employee constructor
    public SecurityUser(Employee employee) {
        this.username = employee.getEmail();
        this.password = employee.getPassword();
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + employee.getRole().name())
        );
    }

    // Client constructor
    public SecurityUser(Client client) {
        this.username = client.getEmail();
        this.password = client.getPin();
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
