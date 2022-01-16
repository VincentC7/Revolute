package fr.miage.choquert.entities.operation;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperationMerchant {

    private String message;
    private BigDecimal ammout;
    private String currency;
    private int port;

}
