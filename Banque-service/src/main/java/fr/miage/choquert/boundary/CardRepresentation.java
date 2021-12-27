package fr.miage.choquert.boundary;

import fr.miage.choquert.assembler.CardAssembler;
import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.card.Card;
import fr.miage.choquert.entities.card.CardInput;
import fr.miage.choquert.repositories.AccountsRepository;
import fr.miage.choquert.repositories.CardsRepository;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "accounts/{accountId}/cards", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Card.class)
public class CardRepresentation {

    private final AccountsRepository accountsRepository;
    private final CardsRepository cardsRepository;
    private final CardAssembler cardAssembler;


    public CardRepresentation(AccountsRepository accountsRepository, CardsRepository cardsRepository, CardAssembler cardAssembler) {
        this.accountsRepository = accountsRepository;
        this.cardsRepository = cardsRepository;
        this.cardAssembler = cardAssembler;
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


}
