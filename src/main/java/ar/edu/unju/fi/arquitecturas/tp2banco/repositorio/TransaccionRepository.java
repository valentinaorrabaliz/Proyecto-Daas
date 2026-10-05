package ar.edu.unju.fi.arquitecturas.tp2banco.repositorio;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Transaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio de acceso a datos para la entidad Transaccion.
 * Registra y consulta los movimientos monetarios (depósitos, extracciones, transferencias)
 * realizados sobre las cuentas bancarias.
 */
@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, UUID> {

    /**
     * Suma el monto total de las extracciones realizadas en el día actual para una cuenta específica.
     * Retorna BigDecimal.ZERO si no existen extracciones registradas.
     */
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t " +
            "WHERE t.cuenta.id = :cuentaId " +
            "AND t.tipo = ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion.EXTRACCION " +
            "AND t.fecha = CURRENT_DATE")
    BigDecimal sumarExtraccionesDelDia(@Param("cuentaId") UUID cuentaId);

    /**
     * Busca todas las transacciones filtradas por su tipo de forma paginada.
     */
    Page<Transaccion> findByTipo(TipoTransaccion tipo, Pageable pageable);

    /**
     * Obtiene el listado de transacciones generadas dentro de un rango de fechas.
     */
    List<Transaccion> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Obtiene el historial completo de transacciones asociadas a una cuenta bancaria específica.
     */
    List<Transaccion> findByCuentaId(UUID cuentaId);

    /**
     * Obtiene las transacciones de una cuenta filtradas por un rango de fechas determinado.
     */
    List<Transaccion> findByCuentaIdAndFechaCreacionBetween(UUID cuentaId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Obtiene los últimos movimientos de una cuenta bancaria ordenados descendentemente por fecha.
     */
    List<Transaccion> findByCuentaIdOrderByFechaCreacionDesc(UUID cuentaId);

}