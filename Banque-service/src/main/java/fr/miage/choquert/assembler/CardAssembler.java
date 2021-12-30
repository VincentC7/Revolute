package fr.miage.choquert.assembler;

import fr.miage.choquert.boundary.AccountRepresentation;
import fr.miage.choquert.boundary.CardRepresentation;
import fr.miage.choquert.entities.card.Card;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class CardAssembler {

    public EntityModel<Card> toModel(Card card, String accountId) {
        return EntityModel.of(card,
                linkTo(methodOn(CardRepresentation.class)
                        .getOneCard(accountId, card.getCardId())).withSelfRel()
        );
    }

    public CollectionModel<EntityModel<Card>> toCollectionModel(Iterable<? extends Card> entities, String accountId) {
        List<EntityModel<Card>> accountModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i,accountId))
                .collect(Collectors.toList());
        return CollectionModel.of(accountModel,
                linkTo(methodOn(CardRepresentation.class)
                        .getAccountCards(accountId)).withSelfRel(),
                linkTo(methodOn(AccountRepresentation.class)
                        .getOneCompte(accountId)).withRel("account")
                );
    }

}
