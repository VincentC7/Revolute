package fr.miage.choquert.boundary;

import fr.miage.choquert.assembler.OperationAssembler;
import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.operation.Operation;
import fr.miage.choquert.repositories.AccountsRepository;
import fr.miage.choquert.repositories.OperationRepository;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "accounts/{accountId}/operations", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Operation.class)
public class OperationRepresentation {

    private final AccountsRepository accountsRepository;
    private final OperationRepository operationRepository;
    private final OperationAssembler operationAssembler;

    public OperationRepresentation(AccountsRepository accountsRepository, OperationRepository operationRepository, OperationAssembler operationAssembler) {
        this.accountsRepository = accountsRepository;
        this.operationRepository = operationRepository;
        this.operationAssembler = operationAssembler;
    }


    //GET /accounts/{accountId}/operations
    @GetMapping
    public ResponseEntity<?> getAccountOperations(@PathVariable("accountId") String accountId) {
        Optional<Account> account = accountsRepository.findById(accountId);
        if (account.isPresent()) {
            return ResponseEntity.ok(operationAssembler.toCollectionModel(
                    operationRepository.findByAccount(account.get()),
                    accountId
            ));
        }
        return ResponseEntity.notFound().build();
    }

    //GET /accounts/{accountId}/operations/{operationId}
    @GetMapping(value = "/{operationId}")
    public ResponseEntity<?> getOneOperation(@PathVariable("accountId") String accountId, @PathVariable("operationId") String operationId) {
        Optional<Account> account = accountsRepository.findById(accountId);
        if(account.isEmpty()) return ResponseEntity.notFound().build();
        return Optional.of(operationRepository.findById(operationId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(operationAssembler.toModel(i.get(), accountId)))
                .orElse(ResponseEntity.notFound().build());
    }
}
