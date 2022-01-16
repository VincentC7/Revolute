package fr.miage.choquert.entities.operation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OperationInput {

    @NotBlank
    @NotNull
    @Size(min = 4, max = 4)
    private String code;

    @NotBlank
    @NotNull
    @Pattern(regexp="[0-9]{16}")
    private String cardNumber;


    @NotBlank
    @NotNull
    private String country;

    @NotBlank
    @NotNull
    @Pattern(regexp="[0-9]{3}")
    private String crypto;


    @NotNull
    private BigDecimal amount;

    @NotNull
    @NotBlank
    private String currency;

    @NotNull
    @NotBlank
    @Pattern(regexp="^[A-Z]{2}[0-9]{22}[A-Z][0-9]{2}?$")
    private String iban;
}
