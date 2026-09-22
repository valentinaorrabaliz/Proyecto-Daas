package ar.edu.unju.fi.arquitecturas.tp2banco.servicios;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.CuentaBancaria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz que define la lógica de negocio para las Cuentas Bancarias.
 */
public interface CuentaBancariaService {

    CuentaBancaria crearCuenta(CuentaBancaria cuenta, UUID clienteId);

    CuentaBancaria obtenerPorId(UUID id);

    CuentaBancaria obtenerPorCbu(String cbu);

    List<CuentaBancaria> obtenerCuentasPorCliente(UUID clienteId);

    Page<CuentaBancaria> listarPorEstado(EstadoCuenta estado, Pageable pageable);

    CuentaBancaria cambiarEstadoCuenta(UUID id, EstadoCuenta nuevoEstado);
}