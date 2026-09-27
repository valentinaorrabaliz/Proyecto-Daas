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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferenciaRequestDto {

    @NotBlank(message = "El CBU de origen es obligatorio")
    @Size(min = 22, max = 22, message = "El CBU de origen debe tener 22 dígitos")
    private String cbuOrigen;

    @NotBlank(message = "El CBU de destino es obligatorio")
    @Size(min = 22, max = 22, message = "El CBU de destino debe tener 22 dígitos")
    private String cbuDestino;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto a transferir debe ser mayor a cero")
    @Digits(integer = 13, fraction = 2, message = "El monto excede el formato permitido")
    private BigDecimal monto;
}