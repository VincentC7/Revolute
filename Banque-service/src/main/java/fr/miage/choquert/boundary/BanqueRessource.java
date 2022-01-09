package fr.miage.choquert.boundary;

import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.card.Card;
import fr.miage.choquert.entities.operation.Operation;
import fr.miage.choquert.entities.operation.OperationInput;
import fr.miage.choquert.entities.operation.OperationMerchant;
import fr.miage.choquert.repositories.AccountsRepository;
import fr.miage.choquert.repositories.CardsRepository;
import fr.miage.choquert.repositories.OperationRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@Tag(name = "Marchant-Ressource", description = "methodes of payment API ressources")
public class BanqueRessource {

    private final Environment environment;
    private final OperationRepository operationRepository;
    private final CardsRepository cardsRepository;
    private final AccountsRepository accountsRepository;

    public BanqueRessource(Environment env, OperationRepository operationRepository, CardsRepository cardsRepository, AccountsRepository accountsRepository) {
        this.environment = env;
        this.operationRepository = operationRepository;
        this.cardsRepository = cardsRepository;
        this.accountsRepository = accountsRepository;
    }

    @PostMapping("/pay")
    public OperationMerchant pay(@RequestBody @Valid OperationInput operationInput){
        Optional<Card> cardRequest = cardsRepository.findByCardNumber(operationInput.getCardNumber());
        String message = "Paiement refusée";
        BigDecimal operationAmmount = new BigDecimal(0);
        if (cardRequest.isPresent()) {
            Card card = cardRequest.get();
            if (!operationInput.getCode().equals(card.getCode())) {
                message += ", mauvais code";
            } else if (!operationInput.getCrypto().equals(card.getCryptogram() + "")) {
            } else {
                Account account = card.getAccount();
                if (account.getBalance().compareTo(operationInput.getAmount()) > 0){
                    message = "Paiment acceptée";
                    operationAmmount = operationInput.getAmount();
                    Operation operation2save = Operation.builder()
                            .operationId(UUID.randomUUID().toString())
                            .datePerformed(Instant.now())
                            .libelle("Achat boutique")
                            .country(operationInput.getCountry())
                            .montant(operationAmmount)
                            .virement(false)
                            .build();
                    Operation saved = operationRepository.save(operation2save);
                    account.setBalance(account.getBalance().subtract(operationAmmount));
                    Account accountSaved = accountsRepository.save(account);
                }
            }
        }

        return OperationMerchant.builder()
                .message(message)
                .ammout(operationAmmount)
                .port(Integer.parseInt(Objects.requireNonNull(environment.getProperty("local.server.port"))))
                .build();
    }
}
