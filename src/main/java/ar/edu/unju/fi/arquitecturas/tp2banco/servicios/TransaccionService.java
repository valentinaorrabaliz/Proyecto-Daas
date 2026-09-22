package ar.edu.unju.fi.arquitecturas.tp2banco.servicios;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Transaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransaccionService {

    Transaccion realizarDeposito(UUID cuentaId, BigDecimal monto, String descripcion);

    Transaccion realizarExtraccion(UUID cuentaId, BigDecimal monto, String descripcion);

    Transaccion obtenerPorId(UUID id);

    List<Transaccion> obtenerHistorialCuenta(UUID cuentaId);

    List<Transaccion> obtenerHistorialCuentaPorFechas(UUID cuentaId, LocalDateTime inicio, LocalDateTime fin);

    Page<Transaccion> listarPorTipo(TipoTransaccion tipo, Pageable pageable);
}