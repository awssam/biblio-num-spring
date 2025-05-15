package uit.fs.bibliotheque.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpruntForm {
    
    @NotNull(message = "La durée d'emprunt est obligatoire")
    @Min(value = 1, message = "La durée d'emprunt doit être au moins 1 jour")
    private Integer dureeEmprunt;
}
