package fr.miage.choquert.repositories;

import fr.miage.choquert.entities.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findBySourceAndTarget(String source, String target);
}
