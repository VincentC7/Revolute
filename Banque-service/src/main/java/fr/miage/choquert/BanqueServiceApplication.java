package fr.miage.choquert;


import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BanqueServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BanqueServiceApplication.class, args);
    }

    @Bean
    public OpenAPI banqueAPI() {
        return new OpenAPI().info(new Info()
                .title("Banque API")
                .version("1.0")
                .description("Documentation sommaire de API Banque 1.0"));
    }
}