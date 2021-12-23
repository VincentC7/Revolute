package fr.miage.choquert.entities.account;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInput {

    @NotBlank
    @NotNull
    @Size(min = 2, max = 32)
    private String name;

    @NotBlank
    @NotNull
    @Size(min = 2, max = 32)
    private String surname;

    @NotBlank
    @NotNull
    @Pattern(regexp="^(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)$")
    private String birthday;

    @NotBlank
    @NotNull
    @Size(min = 2, max = 64)
    private String country;

    @NotBlank
    @NotNull
    @Size(min = 9, max = 9)
    private String passport;

    @NotBlank
    @NotNull
    @Pattern(regexp =  "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$")
    private String tel;

    @NotBlank
    @NotNull
    private String secret;

}
