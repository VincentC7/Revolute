package fr.miage.choquert.boundary;

import fr.miage.choquert.assembler.AccountAssembler;
import fr.miage.choquert.entities.account.AccountValidator;
import fr.miage.choquert.entities.account.Account;

import fr.miage.choquert.entities.account.AccountInput;
import fr.miage.choquert.repositories.AccountsRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.util.ReflectionUtils;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping(value = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Account.class)
public class AccountRepresentation {

    private final AccountsRepository accountsRepository;
    private final AccountAssembler assembler;
    private final AccountValidator validator;

    public AccountRepresentation(AccountsRepository accountsRepository, AccountAssembler assembler, AccountValidator validator) {
        this.accountsRepository = accountsRepository;
        this.assembler = assembler;
        this.validator = validator;
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
        EntityModel<Account> body = assembler.toModel(saved);
        return ResponseEntity.created(location).body(body);
    }

    // PATCH
    @PatchMapping(value = "/{accountId}")
    @Transactional
    public ResponseEntity<?> updateAccountPartiel(@PathVariable("accountId") String accountId, @RequestBody Map<Object, Object> fields) {
        Optional<Account> body = accountsRepository.findById(accountId);
        if (body.isPresent()) {
            Account account = body.get();
            fields.forEach((f, v) -> {
                Field field = ReflectionUtils.findField(Account.class, f.toString());
                assert field != null;
                field.setAccessible(true);
                ReflectionUtils.setField(field, account, v);
            });
            AccountInput accountInput = AccountInput.builder()
                    .name(account.getName()).surname(account.getSurname()).birthday(account.getBirthday())
                    .country(account.getCountry()).passport(account.getPassport()).tel(account.getTel())
                    .secret(account.getSecret())
                    .build();
            System.out.println(accountInput);
            try {
                validator.validate(accountInput);
                account.setId(accountId);
                accountsRepository.save(account);
                return ResponseEntity.ok(assembler.toModel(account));
            }catch (ConstraintViolationException e){
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

}
