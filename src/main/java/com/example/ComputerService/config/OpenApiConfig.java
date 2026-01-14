package com.example.ComputerService.config;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenApiConfig {
    @Value("${app.server.url:http://localhost:8080}")
    private String serverUrl;
    @Bean
    public OpenAPI computerServiceAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Computer Service API")
                        .description("API do zarządzania serwisem komputerowym, zleceniami, dokumentami i płatnościami.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Twój Serwis")
                                .email("kontakt@serwis.pl")
                                .url("https://serwis.pl"))
                )
                .servers(List.of(
                new Server()
                        .url(serverUrl)
                        .description("Prod server")
                ));
    }
}
