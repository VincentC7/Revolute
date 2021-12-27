package fr.miage.choquert.boundary;

import fr.miage.choquert.assembler.CardAssembler;
import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.card.Card;
import fr.miage.choquert.entities.card.CardInput;
import fr.miage.choquert.entities.card.CardValidator;
import fr.miage.choquert.repositories.AccountsRepository;
import fr.miage.choquert.repositories.CardsRepository;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping(value = "accounts/{accountId}/cards", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Card.class)
public class CardRepresentation {

    private final AccountsRepository accountsRepository;
    private final CardsRepository cardsRepository;
    private final CardAssembler cardAssembler;
    private final CardValidator cardValidator;


    public CardRepresentation(AccountsRepository accountsRepository, CardsRepository cardsRepository, CardAssembler cardAssembler, CardValidator cardValidator) {
        this.accountsRepository = accountsRepository;
        this.cardsRepository = cardsRepository;
        this.cardAssembler = cardAssembler;
        this.cardValidator = cardValidator;
    }


    //GET /accounts/{accountId}/cards
    @GetMapping
    public ResponseEntity<?> getAccountCards(@PathVariable("accountId") String accountId) {
        Optional<Account> account = accountsRepository.findById(accountId);
        if (account.isPresent()) {
            return ResponseEntity.ok(cardAssembler.toCollectionModel(
                    cardsRepository.findByAccount(account.get()),
                    accountId
            ));
        }
        return ResponseEntity.notFound().build();
    }

    //GET /accounts/{accountId}/cards/{cardId}
    @GetMapping(value = "/{cardId}")
    public ResponseEntity<?> getOneCard(@PathVariable("accountId") String accountId, @PathVariable("cardId") String cardId) {
        return Optional.of(cardsRepository.findById(cardId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(cardAssembler.toModel(i.get(), accountId)))
                .orElse(ResponseEntity.notFound().build());
    }

    //POST /accounts/{accountId}/cards
    @PostMapping
    @Transactional
    public ResponseEntity<?> saveCard(@PathVariable("accountId") String accountId, @RequestBody @Valid CardInput card)  {
        Optional<Account> account = accountsRepository.findById(accountId);
        if (account.isPresent()){
            Card card2save = Card.builder()
                    .cardId(UUID.randomUUID().toString()).cardNumber(Card.randomCardNumber())
                    .code(card.getCode()).cryptogram(Card.randomCrypto()).ceiling(card.getCeiling())
                    .blocked(card.isBlocked()).contact(card.isContact()).virtual(card.isVirtual())
                    .longitude(card.getLongitude()).latitude(card.getLatitude()).account(account.get())
                    .build();
            Card saved = cardsRepository.save(card2save);
            URI location = linkTo(methodOn(CardRepresentation.class).getOneCard(accountId, saved.getCardId())).toUri();
            return ResponseEntity.created(location).build();
        }
        return ResponseEntity.notFound().build();
    }

    //PATCH /accounts/{accountId}/cards/{cardId}
    @PatchMapping(value = "/{cardId}")
    @Transactional
    public ResponseEntity<?> updateCardPartiel(@PathVariable("accountId") String accountId,
                                               @PathVariable("cardId") String cardId,
                                               @RequestBody Map<Object, Object> fields) {

        Optional<Account> body_account = accountsRepository.findById(accountId);
        if (body_account.isEmpty()) return ResponseEntity.notFound().build();
        Account account = body_account.get();
        Optional<Card> body_card = cardsRepository.findById(cardId);
        if (body_card.isEmpty()) return ResponseEntity.notFound().build();
        Card card = body_card.get();

        try {
            AtomicBoolean badrequest = new AtomicBoolean(false);
            fields.forEach((f, v) -> {
                if (f == "virtual" || f == "contact") {
                    badrequest.set(true);
                }else {
                    Field field = ReflectionUtils.findField(Card.class, f.toString());
                    assert field != null;
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, card, v);
                }
            });
            if (badrequest.get()) return ResponseEntity.badRequest().build();
            CardInput cardInput = CardInput.builder()
                    .code(card.getCode()).ceiling(card.getCeiling()).blocked(card.isBlocked())
                    .latitude(card.getLatitude()).longitude(card.getLongitude())
                    .build();
            cardValidator.validate(cardInput);
            card.setCardId(cardId);
            System.out.println("aaaaa");
            cardsRepository.save(card);
            return ResponseEntity.ok(cardAssembler.toModel(card, accountId));
        }catch (ConstraintViolationException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
