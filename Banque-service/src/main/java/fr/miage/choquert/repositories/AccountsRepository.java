package fr.miage.choquert.repositories;

import fr.miage.choquert.entities.account.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsRepository extends  CrudRepository<Account, String>{}

