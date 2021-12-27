package fr.miage.choquert.entities.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CardInput {

    @NotBlank
    @NotNull
    @Size(min = 4, max = 4)
    private String code;
    private double ceiling;
    private boolean blocked;
    private boolean contact;
    private boolean virtual;
    private double longitude;
    private double latitude;

}
