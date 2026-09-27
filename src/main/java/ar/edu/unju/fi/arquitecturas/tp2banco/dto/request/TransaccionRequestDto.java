package ar.edu.unju.fi.arquitecturas.tp2banco.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionRequestDto {

    @NotNull(message = "El ID de la cuenta es obligatorio")
    private UUID cuentaId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    @Digits(integer = 13, fraction = 2, message = "El monto excede el formato permitido (máximo 13 enteros y 2 decimales)")
    private BigDecimal monto;
}