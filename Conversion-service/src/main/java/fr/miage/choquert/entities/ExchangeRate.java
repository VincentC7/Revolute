package fr.miage.choquert.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {

    @Id
    private Long id;

    @Column(name = "devise_source")
    private String source;

    @Column(name = "devise_target")
    private String target;

    @Column(name = "rate")
    private BigDecimal rate;

}
