package fr.miage.choquert.assembler;

import fr.miage.choquert.boundary.AccountRepresentation;
import fr.miage.choquert.boundary.CardRepresentation;
import fr.miage.choquert.boundary.OperationRepresentation;
import fr.miage.choquert.entities.card.Card;
import fr.miage.choquert.entities.operation.Operation;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OperationAssembler {

    public EntityModel<Operation> toModel(Operation operation, String accountId) {
        return EntityModel.of(operation,
                linkTo(methodOn(OperationRepresentation.class)
                        .getOneOperation(accountId, operation.getOperationId())).withSelfRel()
        );
    }

    public CollectionModel<EntityModel<Operation>> toCollectionModel(Iterable<? extends Operation> entities, String accountId) {
        List<EntityModel<Operation>> operationModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i,accountId))
                .collect(Collectors.toList());
        return CollectionModel.of(operationModel,
                linkTo(methodOn(CardRepresentation.class)
                        .getAccountCards(accountId)).withSelfRel()
        );
    }

}
