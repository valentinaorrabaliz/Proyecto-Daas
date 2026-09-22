package ar.edu.unju.fi.arquitecturas.tp2banco.servicios.impl;

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
    public Transaccion realizarDeposito(UUID cuentaId, BigDecimal monto, String descripcion) {
        validarMonto(monto);
        CuentaBancaria cuenta = obtenerCuentaActiva(cuentaId);

        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        cuentaRepository.save(cuenta);

        return guardarTransaccion(TipoTransaccion.DEPOSITO, monto);
    }

    @Override
    @Transactional
    public Transaccion realizarExtraccion(UUID cuentaId, BigDecimal monto, String descripcion) {
        validarMonto(monto);
        CuentaBancaria cuenta = obtenerCuentaActiva(cuentaId);

        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Saldo actual: " + cuenta.getSaldo());
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
        cuentaRepository.save(cuenta);

        return guardarTransaccion(TipoTransaccion.EXTRACCION, monto);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaccion obtenerPorId(UUID id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerHistorialCuenta(UUID cuentaId) {
        validarExistenciaCuenta(cuentaId);
        return transaccionRepository.findByCuentaOrigenIdOrderByFechaCreacionDesc(cuentaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerHistorialCuentaPorFechas(UUID cuentaId, LocalDateTime inicio, LocalDateTime fin) {
        validarExistenciaCuenta(cuentaId);
        return transaccionRepository.findByCuentaOrigenIdAndFechaCreacionBetween(cuentaId, inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaccion> listarPorTipo(TipoTransaccion tipo, Pageable pageable) {
        return transaccionRepository.findByTipo(tipo, pageable);
    }

    // --- Métodos Auxiliares ---

    private Transaccion guardarTransaccion(TipoTransaccion tipo, BigDecimal monto) {
        Transaccion t = new Transaccion();
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
}