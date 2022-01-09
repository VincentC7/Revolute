package fr.miage.choquert.boundary;

import fr.miage.choquert.assembler.OperationAssembler;
import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.card.Card;
import fr.miage.choquert.entities.operation.Operation;
import fr.miage.choquert.repositories.AccountsRepository;
import fr.miage.choquert.repositories.OperationRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Operations", description = "the operation API ressources")
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
    @io.swagger.v3.oas.annotations.Operation(summary = "Find account operations by accountID", description = "Returns all account operations", tags = { "account", "operation" })
    @Parameter(in = ParameterIn.PATH, name = "accountId", description = "Account id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = Operation.class))),
            @ApiResponse(responseCode = "404", description = "account not found") }
    )
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
    @io.swagger.v3.oas.annotations.Operation(summary = "Find account operation by ID", description = "Returns a single operation", tags = { "account", "operation" })
    @Parameters(value = {
            @Parameter(in = ParameterIn.PATH, name = "accountId", description = "Account id"),
            @Parameter(in = ParameterIn.PATH, name = "operationId", description = "Operation id")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = Operation.class))),
            @ApiResponse(responseCode = "404", description = "account or card not found") }
    )
    @GetMapping(value = "/{operationId}")
    public ResponseEntity<?> getOneOperation(@PathVariable("accountId") String accountId, @PathVariable("operationId") String operationId) {
        Optional<Account> account = accountsRepository.findById(accountId);
        if(account.isEmpty()) return ResponseEntity.notFound().build();
        return Optional.of(operationRepository.findById(operationId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(operationAssembler.toModel(i.get(), accountId)))
                .orElse(ResponseEntity.notFound().build());
    }
}
