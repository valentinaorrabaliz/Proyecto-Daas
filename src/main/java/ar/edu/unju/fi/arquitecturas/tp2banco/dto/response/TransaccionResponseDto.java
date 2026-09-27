package ar.edu.unju.fi.arquitecturas.tp2banco.dto.response;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoProcesamiento;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionResponseDto {

    private UUID id;
    private BigDecimal monto;
    private TipoTransaccion tipo;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoProcesamiento estadoProcesamiento;
    private UUID cuentaId;
}