package ar.edu.unju.fi.arquitecturas.tp2banco.dto.response;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoProcesamiento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferenciaResponseDto {

    private UUID idTransaccion;
    private String cbuOrigen;
    private String cbuDestino;
    private BigDecimal monto;
    private LocalDateTime fechaHora;
    private EstadoProcesamiento estado;
    private String mensaje;
}