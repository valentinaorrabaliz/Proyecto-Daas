package ar.edu.unju.fi.arquitecturas.tp2banco.repositorio;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.CuentaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de acceso a datos para la entidad CuentaBancaria.
 * Gestiona las operaciones de persistencia para las cuentas del banco
 * (Cajas de Ahorro y Cuentas Corrientes).
 */

@Repository
public interface CuentaBancariaRepository extends JpaRepository<CuentaBancaria, UUID> {


    /**
     * Busca una cuenta bancaria a partir de su CBU.
     */
    Optional<CuentaBancaria> findByCbu(String cbu);


    /**
     * Obtiene el listado de todas las cuentas bancarias filtradas por su estado actual.
     */
    List<CuentaBancaria> findByEstado(EstadoCuenta estado);


    /**
     * Verifica la existencia de una cuenta a través de su CBU.
     */
    boolean existsByCbu(String cbu);


    /**
     * Obtiene las cuentas bancarias asociadas a un cliente específico.
     */
    List<CuentaBancaria> findByClienteId(UUID clienteId);


    /**
     * Obtiene las cuentas bancarias por cbu y estado para evitar movimientos de cuentas suspendidas.
     */
    Optional<CuentaBancaria> findByCbuAndEstado(String cbu, EstadoCuenta estado);

}