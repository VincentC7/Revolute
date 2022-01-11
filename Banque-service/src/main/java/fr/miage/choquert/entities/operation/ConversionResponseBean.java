package fr.miage.choquert.entities.operation;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversionResponseBean {

    private String message;
    private BigDecimal beforeConversion;
    private BigDecimal afterConversion;
    private BigDecimal rate;

}
