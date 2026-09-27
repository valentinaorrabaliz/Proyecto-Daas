package ar.edu.unju.fi.arquitecturas.tp2banco.servicios.impl;

import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.TransaccionRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.TransaccionResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoProcesamiento;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.OperacionNoPermitidaException;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.SaldoInsuficienteException;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Transaccion;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.TransaccionRepository;
import ar.edu.unju.fi.arquitecturas.tp2banco.servicios.TransaccionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final CuentaBancariaRepository cuentaRepository;

    public TransaccionServiceImpl(TransaccionRepository transaccionRepository, CuentaBancariaRepository cuentaRepository) {
        this.transaccionRepository = transaccionRepository;
        this.cuentaRepository = cuentaRepository;
    }

    @Override
    @Transactional
    public TransaccionResponseDto realizarDeposito(TransaccionRequestDto transaccionRequest) {
        validarMonto(transaccionRequest.getMonto());
        CuentaBancaria cuenta = obtenerCuentaActiva(transaccionRequest.getCuentaId());

        cuenta.setSaldo(cuenta.getSaldo().add(transaccionRequest.getMonto()));
        cuentaRepository.save(cuenta);

        Transaccion t = guardarTransaccion(cuenta, TipoTransaccion.DEPOSITO, transaccionRequest.getMonto());
        return mapToResponseDto(t);
    }

    @Override
    @Transactional
    public TransaccionResponseDto realizarExtraccion(TransaccionRequestDto transaccionRequest) {
        validarMonto(transaccionRequest.getMonto());
        CuentaBancaria cuenta = obtenerCuentaActiva(transaccionRequest.getCuentaId());

        if (cuenta.getSaldo().compareTo(transaccionRequest.getMonto()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Saldo actual: " + cuenta.getSaldo());
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(transaccionRequest.getMonto()));
        cuentaRepository.save(cuenta);

        Transaccion t = guardarTransaccion(cuenta, TipoTransaccion.EXTRACCION, transaccionRequest.getMonto());
        return mapToResponseDto(t);
    }

    @Override
    @Transactional(readOnly = true)
    public TransaccionResponseDto obtenerPorId(UUID id) {
        Transaccion t = transaccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción no encontrada con ID: " + id));
        return mapToResponseDto(t);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransaccionResponseDto> obtenerHistorialCuenta(UUID cuentaId) {
        validarExistenciaCuenta(cuentaId);
        return transaccionRepository.findByCuentaIdOrderByFechaCreacionDesc(cuentaId)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransaccionResponseDto> obtenerHistorialCuentaPorFechas(UUID cuentaId, LocalDateTime inicio, LocalDateTime fin) {
        validarExistenciaCuenta(cuentaId);
        return transaccionRepository.findByCuentaIdAndFechaCreacionBetween(cuentaId, inicio, fin)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransaccionResponseDto> listarPorTipo(TipoTransaccion tipo, Pageable pageable) {
        return transaccionRepository.findByTipo(tipo, pageable)
                .map(this::mapToResponseDto);
    }

    // --- Métodos Auxiliares ---

    private Transaccion guardarTransaccion(CuentaBancaria cuenta, TipoTransaccion tipo, BigDecimal monto) {
        Transaccion t = new Transaccion();
        t.setCuenta(cuenta);
        t.setTipo(tipo);
        t.setMonto(monto);
        t.setFecha(LocalDate.now());
        t.setHora(LocalTime.now());
        t.setEstadoProcesamiento(EstadoProcesamiento.COMPLETADA);
        return transaccionRepository.save(t);
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperacionNoPermitidaException("El monto debe ser mayor a cero.");
        }
    }

    private void validarExistenciaCuenta(UUID cuentaId) {
        if (!cuentaRepository.existsById(cuentaId)) {
            throw new RecursoNoEncontradoException("La cuenta no existe.");
        }
    }

    private CuentaBancaria obtenerCuentaActiva(UUID cuentaId) {
        CuentaBancaria cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada con ID: " + cuentaId));

        if (cuenta.getEstado() != EstadoCuenta.ACTIVA) {
            throw new OperacionNoPermitidaException("La cuenta no está ACTIVA.");
        }
        return cuenta;
    }

    private TransaccionResponseDto mapToResponseDto(Transaccion t) {
        return TransaccionResponseDto.builder()
                .id(t.getId())
                .monto(t.getMonto())
                .tipo(t.getTipo())
                .fecha(t.getFecha())
                .hora(t.getHora())
                .estadoProcesamiento(t.getEstadoProcesamiento())
                .cuentaId(t.getCuenta() != null ? t.getCuenta().getId() : null)
                .build();
    }
}