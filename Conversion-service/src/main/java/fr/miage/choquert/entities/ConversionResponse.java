package fr.miage.choquert.entities;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversionResponse {

    private BigDecimal beforeConversion;
    private BigDecimal afterConversion;
    private BigDecimal rate;

}
