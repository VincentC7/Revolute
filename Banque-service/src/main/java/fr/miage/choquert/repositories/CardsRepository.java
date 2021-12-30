package fr.miage.choquert.repositories;

import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.card.Card;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardsRepository extends CrudRepository<Card, String> {

    @Query("select c from Card c where c.account = ?1")
    Iterable<? extends Card> findByAccount(Account account);
}
