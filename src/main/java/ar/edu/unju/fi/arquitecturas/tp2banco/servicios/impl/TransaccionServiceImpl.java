package ar.edu.unju.fi.arquitecturas.tp2banco.servicios.impl;

import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.ConfiguracionParametroRepository;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.TransaccionRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.TransferenciaRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.TransaccionResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.TransferenciaResponseDto;
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
    private final ConfiguracionParametroRepository parametroRepository;

    public TransaccionServiceImpl(TransaccionRepository transaccionRepository, CuentaBancariaRepository cuentaRepository, ConfiguracionParametroRepository parametroRepository) {
        this.transaccionRepository = transaccionRepository;
        this.cuentaRepository = cuentaRepository;
        this.parametroRepository = parametroRepository;
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
        Cliente cliente = cuenta.getCliente();

        // 1. Determinar si pertenece a grupo familiar (Titular con adherentes o Adherente)
        boolean esGrupoFamiliar = (cliente.getTitular() != null) ||
                (cliente.getAdherentes() != null && !cliente.getAdherentes().isEmpty());

        String claveTope = esGrupoFamiliar ? "TOPE_DIARIO_GRUPO_FAMILIAR" : "TOPE_DIARIO_INDIVIDUAL";
        BigDecimal topePorDefecto = esGrupoFamiliar ? new BigDecimal("70000.00") : new BigDecimal("100000.00");

        BigDecimal topeDiario = parametroRepository.findById(claveTope)
                .map(p -> new BigDecimal(p.getValor()))
                .orElse(topePorDefecto);

        // 2. Controlar la suma acumulada de extracciones del día
        BigDecimal extraidoHoy = transaccionRepository.sumarExtraccionesDelDia(cuenta.getId());
        BigDecimal totalConEstaExtraccion = extraidoHoy.add(transaccionRequest.getMonto());

        if (totalConEstaExtraccion.compareTo(topeDiario) > 0) {
            throw new OperacionNoPermitidaException(
                    "Límite diario de extracción superado. Tope máximo: $" + topeDiario +
                            ", Extraído hoy: $" + extraidoHoy +
                            ", Intento actual: $" + transaccionRequest.getMonto()
            );
        }

        // 3. Validar saldo suficiente
        if (cuenta.getSaldo().compareTo(transaccionRequest.getMonto()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Saldo actual: " + cuenta.getSaldo());
        }

        // 4. Efectuar débito y registrar transacción
        cuenta.setSaldo(cuenta.getSaldo().subtract(transaccionRequest.getMonto()));
        cuentaRepository.save(cuenta);

        Transaccion t = guardarTransaccion(cuenta, TipoTransaccion.EXTRACCION, transaccionRequest.getMonto());
        return mapToResponseDto(t);
    }

    @Override
    @Transactional
    public TransferenciaResponseDto realizarTransferencia(TransferenciaRequestDto dto) {
        validarMonto(dto.getMonto());

        if (dto.getCbuOrigen().equalsIgnoreCase(dto.getCbuDestino())) {
            throw new OperacionNoPermitidaException("No se puede realizar una transferencia al mismo CBU de origen.");
        }

        CuentaBancaria cuentaOrigen = cuentaRepository.findByCbu(dto.getCbuOrigen())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta de origen no encontrada con CBU: " + dto.getCbuOrigen()));

        CuentaBancaria cuentaDestino = cuentaRepository.findByCbu(dto.getCbuDestino())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta de destino no encontrada con CBU: " + dto.getCbuDestino()));

        if (cuentaOrigen.getEstado() != EstadoCuenta.ACTIVA) {
            throw new OperacionNoPermitidaException("La cuenta de origen no está ACTIVA.");
        }
        if (cuentaDestino.getEstado() != EstadoCuenta.ACTIVA) {
            throw new OperacionNoPermitidaException("La cuenta de destino no está ACTIVA.");
        }

        if (cuentaOrigen.getSaldo().compareTo(dto.getMonto()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Saldo disponible: " + cuentaOrigen.getSaldo());
        }

        // Modificación de saldos
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(dto.getMonto()));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(dto.getMonto()));

        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);

        // Registro de ambas transacciones para el historial de cada cuenta
        Transaccion transaccionOrigen = guardarTransaccion(cuentaOrigen, TipoTransaccion.TRANSFERENCIA_ENVIADA, dto.getMonto());
        guardarTransaccion(cuentaDestino, TipoTransaccion.TRANSFERENCIA_RECIBIDA, dto.getMonto());

        return TransferenciaResponseDto.builder()
                .idTransaccion(transaccionOrigen.getId())
                .cbuOrigen(dto.getCbuOrigen())
                .cbuDestino(dto.getCbuDestino())
                .monto(dto.getMonto())
                .fechaHora(LocalDateTime.of(transaccionOrigen.getFecha(), transaccionOrigen.getHora()))
                .estado(EstadoProcesamiento.COMPLETADA)
                .mensaje("Transferencia realizada con éxito.")
                .build();
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