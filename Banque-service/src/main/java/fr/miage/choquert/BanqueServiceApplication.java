package fr.miage.choquert;


import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@LoadBalancerClients({
        @LoadBalancerClient(name = "Conversion-service", configuration = ClientConfiguration.class)
})
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

    @Bean
    RestTemplate template() {
        return new RestTemplate();
    }
}