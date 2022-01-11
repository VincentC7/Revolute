package fr.miage.choquert.boundary;

import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.card.Card;
import fr.miage.choquert.entities.operation.ConversionResponseBean;
import fr.miage.choquert.entities.operation.Operation;
import fr.miage.choquert.entities.operation.OperationInput;
import fr.miage.choquert.entities.operation.OperationMerchant;
import fr.miage.choquert.repositories.AccountsRepository;
import fr.miage.choquert.repositories.CardsRepository;
import fr.miage.choquert.repositories.OperationRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

    RestTemplate template;
    LoadBalancerClientFactory clientFactory;

    public BanqueRessource(RestTemplate rt, LoadBalancerClientFactory lbcf,Environment env, OperationRepository operationRepository, CardsRepository cardsRepository, AccountsRepository accountsRepository) {
        this.environment = env;
        this.operationRepository = operationRepository;
        this.cardsRepository = cardsRepository;
        this.accountsRepository = accountsRepository;
        this.template = rt;
        this.clientFactory = lbcf;
    }

    @PostMapping("/pay")
    public OperationMerchant pay(@RequestBody @Valid OperationInput operationInput){
        Optional<Operation> operation = verif_operation(operationInput);
        String message = "Paiement refusée";
        BigDecimal operationAmmount = new BigDecimal(0);
        if (operation.isPresent()){
            Operation o2save = operation.get();
            message = "Paiment acceptée";
            operationAmmount = o2save.getMontant();
            operationRepository.save(o2save);
            Account account = o2save.getAccount();
            account.setBalance(account.getBalance().subtract(o2save.getMontant()));
            accountsRepository.save(account);
        }

        return OperationMerchant.builder()
                .message(message)
                .ammout(operationAmmount)
                .currency(operationInput.getCurrency())
                .port(Integer.parseInt(Objects.requireNonNull(environment.getProperty("local.server.port"))))
                .build();
    }

    private Optional<Operation> verif_operation(OperationInput operationInput) {
        Optional<Card> card = verifCard(operationInput.getCardNumber(), operationInput.getCode(), operationInput.getCrypto());
        if (card.isEmpty()) return Optional.empty();
        Pair<BigDecimal, BigDecimal> operationAmount = calcOperationAmount(operationInput.getCurrency(), operationInput.getAmount());
        if (operationAmount.getSecond().equals(new BigDecimal(-1))) return Optional.empty();
        Optional<Account> account = verifAccountBalance(card.get().getAccount(), operationAmount.getFirst());
        if (account.isEmpty()) return Optional.empty();

        Operation operation = Operation.builder()
                .operationId(UUID.randomUUID().toString())
                .datePerformed(Instant.now()).account(account.get())
                .libelle("Achat boutique").country(operationInput.getCountry())
                .montant(operationAmount.getFirst()).rate(operationAmount.getSecond())
                .ibanCrediteur(operationInput.getIban()).virement(false)
                .build();
        return Optional.of(operation);
    }

    private Optional<Card> verifCard(String cardNumber, String code, String crypto) {
        Optional<Card> cardRequest = cardsRepository.findByCardNumber(cardNumber);
        if (cardRequest.isPresent()) {
            Card card = cardRequest.get();
            if (card.getCode().equals(code) && card.getCryptogram().equals(crypto)) {
                return Optional.of(card);
            }
        }
        return Optional.empty();
    }


    private Optional<Account> verifAccountBalance(Account account, BigDecimal operationAmount) {
        if (account.getBalance().compareTo(operationAmount) <= 0){
            return Optional.empty();
        }
        return Optional.of(account);
    }

    public Pair<BigDecimal,BigDecimal> calcOperationAmount(String operationCurrency, BigDecimal baseAmount) {
        if (operationCurrency.equals(Account.ACCOUNT_DEVISE)) {
            return Pair.of(baseAmount, new BigDecimal(1));
        }
        ConversionResponseBean conversionResponseBean = getExchangeRate(Account.ACCOUNT_DEVISE, operationCurrency, baseAmount);
        if (conversionResponseBean.getRate().equals(new BigDecimal(-1))){
            return Pair.of(baseAmount, conversionResponseBean.getRate());
        }
        return Pair.of(conversionResponseBean.getAfterConversion(),conversionResponseBean.getRate());
    }

    @CircuitBreaker(name = "Conversion-service", fallbackMethod = "fallbackConversionCall")
    @Retry(name = "fallbackConversionCall", fallbackMethod = "fallbackConversionCall")
    private ConversionResponseBean getExchangeRate(String source, String target, BigDecimal ammount) {
        RoundRobinLoadBalancer lb = clientFactory.getInstance("Conversion-service", RoundRobinLoadBalancer.class);
        ServiceInstance instance = lb.choose().block().getServer();
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/conversion-devise/source/{source}/target/{target}/amount/{amout}";
        ConversionResponseBean response = template.getForObject(url, ConversionResponseBean.class, source, target, ammount);
        return ConversionResponseBean.builder()
                .message(response.getMessage())
                .rate(response.getRate())
                .afterConversion(response.getAfterConversion())
                .build();
    }

}
