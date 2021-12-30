package fr.miage.choquert.repositories;

import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.operation.Operation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends CrudRepository<Operation, String>  {

    Iterable<? extends Operation> findByAccount(Account account);
}
