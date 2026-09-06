package ar.edu.unju.fi.arquitecturas.tp2banco.repositorio;


import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Transaccion;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    // Query Method 1: Buscar transacciones por su tipo (DEPOSITO, EXTRACCION, etc.)
    List<Transaccion> findByTipo(TipoTransaccion tipo);

    // Query Method 2: Obtener transacciones generadas dentro de un rango de fechas
    List<Transaccion> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
}