package fr.miage.choquert.boundary;

import fr.miage.choquert.assembler.AccountAssembler;
import fr.miage.choquert.entities.account.Account;

import fr.miage.choquert.entities.account.AccountInput;
import fr.miage.choquert.repositories.AccountsRepository;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping(value = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Account.class)
public class AccountRepresentation {

    private final AccountsRepository accountsRepository;
    private final AccountAssembler assembler;

    public AccountRepresentation(AccountsRepository accountsRepository, AccountAssembler assembler) {
        this.accountsRepository = accountsRepository;
        this.assembler = assembler;
    }

    // GET /accounts/{accountId}
    @GetMapping(value = "/{accountId}")
    public ResponseEntity<?> getOneCompte(@PathVariable("accountId") String accountId) {
        return Optional.of(accountsRepository.findById(accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountInput account)  {
        String iban = Account.randomIBAN();
        Account account2save = Account.builder()
                .id(UUID.randomUUID().toString())
                .iban(iban).accountNumber(iban.substring(14,25))
                .name(account.getName()).surname(account.getSurname()).birthday(account.getBirthday())
                .country(account.getCountry()).passport(account.getPassport()).tel(account.getTel())
                .secret(account.getSecret()).balance(0.0)
                .build();
        Account saved = accountsRepository.save(account2save);
        URI location = linkTo(AccountRepresentation.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

}
