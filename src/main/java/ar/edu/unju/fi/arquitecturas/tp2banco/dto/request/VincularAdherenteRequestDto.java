package ar.edu.unju.fi.arquitecturas.tp2banco.dto.request;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.RolFamiliar;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VincularAdherenteRequestDto {

    @NotNull(message = "El ID del titular es obligatorio")
    private UUID titularId;

    @NotNull(message = "El ID del adherente es obligatorio")
    private UUID adherenteId;

    @NotNull(message = "El rol familiar es obligatorio")
    private RolFamiliar rolFamiliar;
}
