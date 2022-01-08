package fr.miage.choquert.boundary;

import fr.miage.choquert.entities.operation.Operation;
import fr.miage.choquert.entities.operation.OperationMerchant;
import fr.miage.choquert.repositories.OperationRepository;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Objects;

@RestController
public class BanqueRessource {

    private final Environment environment;
    private final OperationRepository operationRepository;

    public BanqueRessource(Environment env, OperationRepository operationRepository) {
        this.environment = env;
        this.operationRepository = operationRepository;
    }

    @GetMapping("/pay/card/{number}/code/{code}/ammount/{ammout}")
    public OperationMerchant getValeurDeChange(@PathVariable String number, @PathVariable String code, @PathVariable BigDecimal ammout) {
        return OperationMerchant.builder()
                .id(1L)
                .message("Paiement acceptée")
                .ammout(ammout)
                .port(Integer.parseInt(Objects.requireNonNull(environment.getProperty("local.server.port"))))
                .build();
    }

}
