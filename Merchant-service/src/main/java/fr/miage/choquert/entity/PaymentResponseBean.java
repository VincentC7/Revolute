package fr.miage.choquert.entity;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseBean {

    private String message;
    private BigDecimal ammout;
    private int port;

}
