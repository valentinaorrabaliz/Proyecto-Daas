package ar.edu.unju.fi.arquitecturas.tp2banco.servicios;

import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.CuentaBancariaRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.CuentaBancariaResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz que define la lógica de negocio para las Cuentas Bancarias.
 */
public interface CuentaBancariaService {

    CuentaBancariaResponseDto crearCuenta(CuentaBancariaRequestDto cuentaRequest);

    CuentaBancariaResponseDto obtenerPorId(UUID id);

    CuentaBancariaResponseDto obtenerPorCbu(String cbu);

    List<CuentaBancariaResponseDto> obtenerCuentasPorCliente(UUID clienteId);

    Page<CuentaBancariaResponseDto> listarPorEstado(EstadoCuenta estado, Pageable pageable);

    CuentaBancariaResponseDto cambiarEstadoCuenta(UUID id, EstadoCuenta nuevoEstado);
}