package ar.edu.unju.fi.arquitecturas.tp2banco.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CuentaBancariaRequestDto {

    @NotNull(message = "El ID del cliente es obligatorio")
    private UUID clienteId;

    @NotBlank(message = "El CBU es obligatorio")
    @Size(min = 22, max = 22, message = "El CBU debe tener exactamente 22 dígitos")
    private String cbu;

    @Size(max = 40, message = "El alias no puede superar los 40 caracteres")
    private String alias;

    @NotNull(message = "El saldo inicial es obligatorio")
    @DecimalMin(value = "0.0", message = "El saldo inicial no puede ser negativo")
    @Digits(integer = 13, fraction = 2, message = "El saldo excede el formato permitido (máximo 13 enteros y 2 decimales)")
    private BigDecimal saldoInicial;
}