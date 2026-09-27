package ar.edu.unju.fi.arquitecturas.tp2banco.servicios;

import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.TransaccionRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.TransferenciaRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.TransaccionResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.TransferenciaResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransaccionService {

    TransaccionResponseDto realizarDeposito(TransaccionRequestDto transaccionRequest);

    TransaccionResponseDto realizarExtraccion(TransaccionRequestDto transaccionRequest);

    TransaccionResponseDto obtenerPorId(UUID id);

    List<TransaccionResponseDto> obtenerHistorialCuenta(UUID cuentaId);

    List<TransaccionResponseDto> obtenerHistorialCuentaPorFechas(UUID cuentaId, LocalDateTime inicio, LocalDateTime fin);

    Page<TransaccionResponseDto> listarPorTipo(TipoTransaccion tipo, Pageable pageable);

    TransferenciaResponseDto realizarTransferencia(TransferenciaRequestDto transferenciaRequest);
}