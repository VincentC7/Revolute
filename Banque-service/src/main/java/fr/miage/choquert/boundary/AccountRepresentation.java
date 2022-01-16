package fr.miage.choquert.boundary;

import fr.miage.choquert.assembler.AccountAssembler;
import fr.miage.choquert.entities.account.AccountValidator;
import fr.miage.choquert.entities.account.Account;

import fr.miage.choquert.entities.account.AccountInput;
import fr.miage.choquert.repositories.AccountsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


@RestController
@RequestMapping(value = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Account.class)
@Tag(name = "Accounts", description = "the account API ressources")
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
    @Operation(summary = "Find account by ID", description = "Returns a single account", tags = { "account" })
    @Parameter(in = ParameterIn.PATH, name = "accountId", description = "Account Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "404", description = "Account not found") }
    )
    @GetMapping(value = "/{accountId}")
    public ResponseEntity<?> getOneCompte(@PathVariable("accountId") String accountId) {
        return Optional.of(accountsRepository.findById(accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get()))).orElse(ResponseEntity.notFound().build());
    }

    // POST /accounts
    @Operation(summary = "Create new account", description = "create a new account and insert it in database", tags = { "account" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created",
                    content = @Content(schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
    })
    @PostMapping
    @Transactional
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountInput account)  {
        String iban = Account.randomIBAN();
        Account account2save = Account.builder()
                .accountId(UUID.randomUUID().toString())
                .iban(iban).accountNumber(iban.substring(14,25))
                .name(account.getName()).surname(account.getSurname()).birthday(account.getBirthday())
                .country(account.getCountry()).passport(account.getPassport()).tel(account.getTel())
                .secret(account.getSecret()).balance(new BigDecimal(1000))
                .build();
        Account saved = accountsRepository.save(account2save);
        URI location = linkTo(AccountRepresentation.class).slash(saved.getAccountId()).toUri();
        return ResponseEntity.created(location).build();
    }

    // PATCH
    @Operation(summary = "Update an existing account", description = "", tags = { "account" })
    @Parameter(in = ParameterIn.PATH, name = "accountId", description = "Account Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PatchMapping(value = "/{accountId}")
    @Transactional
    public ResponseEntity<?> updateAccountPartiel(@PathVariable("accountId") String accountId, @RequestBody Map<Object, Object> fields) {
        Optional<Account> body = accountsRepository.findById(accountId);
        if (body.isPresent()) {
            Account account = body.get();
            AtomicBoolean badrequest = new AtomicBoolean(false);
            String[] immutableParams = {"accountId","iban","accountNumber","balance"};
            fields.forEach((f, v) -> {
                if (Arrays.asList(immutableParams).contains(f.toString())) {
                    badrequest.set(true);
                }else {
                    Field field = ReflectionUtils.findField(Account.class, f.toString());
                    if (field == null){
                        badrequest.set(true);
                    }else{
                        field.setAccessible(true);
                        ReflectionUtils.setField(field, account, v);
                    }
                }
            });
            if (badrequest.get()) return ResponseEntity.badRequest().build();
            AccountInput accountInput = AccountInput.builder()
                    .name(account.getName()).surname(account.getSurname()).birthday(account.getBirthday())
                    .country(account.getCountry()).passport(account.getPassport()).tel(account.getTel())
                    .secret(account.getSecret())
                    .build();
            try {
                validator.validate(accountInput);
                account.setAccountId(accountId);
                accountsRepository.save(account);
                return ResponseEntity.ok(assembler.toModel(account));
            }catch (ConstraintViolationException | IllegalArgumentException e){
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    // GET /accounts/{accountId}/balance
    @GetMapping(value = "/{accountId}/balance")
    @Operation(summary = "Get balance of an existing account", description = "", tags = { "account" })
    @Parameter(in = ParameterIn.PATH, name = "accountId", description = "Account Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<?> getAccountBalance(@PathVariable("accountId") String accountId) {
        return Optional.of(accountsRepository.findById(accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok().body(
                        "{\n" +
                                "\t\"balance\" :"+i.get().getBalance()+",\n" +
                        "}"
                )).orElse(ResponseEntity.notFound().build());
    }

}
