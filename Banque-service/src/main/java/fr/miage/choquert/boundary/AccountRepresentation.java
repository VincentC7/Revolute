package fr.miage.choquert.boundary;

import fr.miage.choquert.assembler.AccountAssembler;
import fr.miage.choquert.entities.account.Account;

import fr.miage.choquert.repositories.AccountsRepository;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.Optional;


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

}
