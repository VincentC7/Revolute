package fr.miage.choquert.boundary;

import fr.miage.choquert.entities.operation.OperationInput;
import fr.miage.choquert.entities.operation.OperationMerchant;
import fr.miage.choquert.repositories.OperationRepository;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PostMapping("/pay")
    public OperationMerchant pay(@RequestBody @Valid OperationInput operationInput){
        System.out.println(operationInput);
        return OperationMerchant.builder()
                .id(1L)
                .message("Paiement acceptée")
                .ammout(new BigDecimal(1))
                .port(Integer.parseInt(Objects.requireNonNull(environment.getProperty("local.server.port"))))
                .build();
    }
}
