package ar.edu.unju.fi.arquitecturas.tp2banco.dto.response;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCuenta;
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
public class CuentaBancariaResponseDto {

    private UUID id;
    private String cbu;
    private String alias;
    private BigDecimal saldo;
    private EstadoCuenta estado;
    private UUID clienteId;
}