package fr.miage.choquert.boudary;

import fr.miage.choquert.entities.ConversionResponse;
import fr.miage.choquert.entities.ExchangeRate;
import fr.miage.choquert.repositories.ExchangeRateRepository;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
public class ConversionController {

    private final Environment environment;
    private final ExchangeRateRepository exchangeRateRepository;


    public ConversionController(Environment environment, ExchangeRateRepository exchangeRateRepository) {
        this.environment = environment;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @GetMapping("/conversion-devise/source/{source}/target/{target}/amount/{amout}")
    public ConversionResponse convert(@PathVariable String source, @PathVariable String target,
                                      @PathVariable BigDecimal amout) {
        Optional<ExchangeRate> exchangeRateRequest = exchangeRateRepository.findBySourceAndTarget(source, target);

        if (exchangeRateRequest.isEmpty()){
            return ConversionResponse.builder()
                    .rate(new BigDecimal(-1))
                    .build();
        }

        ExchangeRate exchangeRate = exchangeRateRequest.get();
        return ConversionResponse.builder()
                .beforeConversion(amout)
                .afterConversion(amout.multiply(exchangeRate.getRate()))
                .rate(exchangeRate.getRate())
                .build();

    }


}
