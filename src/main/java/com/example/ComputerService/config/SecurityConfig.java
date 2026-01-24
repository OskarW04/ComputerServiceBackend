package com.example.ComputerService.config;

import com.example.ComputerService.security.CustomUserDetailsService;
import com.example.ComputerService.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // Włącza obsługę CORS korzystając z beana zdefiniowanego na dole
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // --- PUBLICZNE ---
                        .requestMatchers("/api/auth/login", "/api/auth/generatePIN").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Swagger UI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // --- MAGAZYN (Warehouse) ---
                        // Wyjątek: Technik musi widzieć listę części, żeby zrobić kosztorys
                        .requestMatchers("/api/warehouse/getAllParts").hasAnyRole("WAREHOUSE", "MANAGER", "TECHNICIAN")
                        // Reszta magazynu tylko dla Magazyniera i Managera
                        .requestMatchers("/api/warehouse/**").hasAnyRole("WAREHOUSE", "MANAGER")

                        // --- TECHNIK (Technician) ---
                        .requestMatchers("/api/tech/**").hasAnyRole("TECHNICIAN", "MANAGER")

                        // --- BIURO (Office) ---
                        .requestMatchers("/api/office/**").hasAnyRole("OFFICE", "MANAGER")

                        // --- ZAMÓWIENIA (Orders) ---
                        // Tworzenie zleceń tylko Biuro/Manager
                        .requestMatchers("/api/order/createOrder").hasAnyRole("OFFICE", "MANAGER")
                        // Przeglądanie zleceń - dostęp szeroki (Biuro, Tech, Manager)
                        .requestMatchers("/api/order/**").hasAnyRole("OFFICE", "MANAGER", "TECHNICIAN")

                        // --- MANAGER (Exclusive) ---
                        // Zarządzanie usługami, pracownikami i przypisywanie zleceń
                        .requestMatchers("/api/manager/**").hasRole("MANAGER")
                        .requestMatchers("/api/employees/**").hasRole("MANAGER")

                        // --- KLIENT (Client) ---
                        .requestMatchers("/api/client/**").hasRole("CLIENT")

                        // --- POZOSTAŁE (np. /api/auth/getMe) ---
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- NAPRAWA CORS ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Dopuszczamy Twój frontend (localhost) i domenę produkcyjną
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",          // Frontend lokalny (Vite/React)
                "https://computerservice.antek.page" // Produkcja
        ));

        // 2. Metody HTTP
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 3. Nagłówki
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));

        // 4. Credentials (wymagane jeśli frontend ma działać stabilnie z autoryzacją)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}