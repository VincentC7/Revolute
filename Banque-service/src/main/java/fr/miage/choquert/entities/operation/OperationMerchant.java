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

    private Long id;
    private String message;
    private BigDecimal ammout;
    private int port;

}
