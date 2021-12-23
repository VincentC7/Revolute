package fr.miage.choquert.assembler;

import fr.miage.choquert.boundary.AccountRepresentation;
import fr.miage.choquert.entities.account.Account;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountAssembler {

    public EntityModel<Account> toModel(Account compte){
        return EntityModel.of(compte,
                linkTo(methodOn(AccountRepresentation.class)
                        .getOneCompte(compte.getId())).withSelfRel());
    }

}
