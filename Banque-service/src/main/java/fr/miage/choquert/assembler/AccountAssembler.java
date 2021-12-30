package fr.miage.choquert.assembler;

import fr.miage.choquert.boundary.AccountRepresentation;
import fr.miage.choquert.boundary.CardRepresentation;
import fr.miage.choquert.boundary.OperationRepresentation;
import fr.miage.choquert.entities.account.Account;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountAssembler {

    public EntityModel<Account> toModel(Account compte){
        return EntityModel.of(compte,
                linkTo(methodOn(AccountRepresentation.class)
                        .getOneCompte(compte.getAccountId())).withSelfRel(),
                linkTo(methodOn(AccountRepresentation.class)
                        .getAccountBalance(compte.getAccountId())).withRel("balance"),
                linkTo(methodOn(CardRepresentation.class)
                        .getAccountCards(compte.getAccountId())).withRel("cards"),
                linkTo(methodOn(OperationRepresentation.class)
                        .getAccountOperations(compte.getAccountId())).withRel("operations")
        );
    }

}
