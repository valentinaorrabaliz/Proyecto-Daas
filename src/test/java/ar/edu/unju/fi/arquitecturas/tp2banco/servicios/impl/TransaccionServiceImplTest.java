package ar.edu.unju.fi.arquitecturas.tp2banco.servicios.impl;

import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.TransaccionRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.TransferenciaRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.TransaccionResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.TransferenciaResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoProcesamiento;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.OperacionNoPermitidaException;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.SaldoInsuficienteException;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.ConfiguracionParametro;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Transaccion;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.ConfiguracionParametroRepository;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para las operaciones financieras.
 * Cubre la lógica de negocio para depósitos, extracciones y transferencias entre cuentas.
 */
@ExtendWith(MockitoExtension.class)
class TransaccionServiceImplTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private CuentaBancariaRepository cuentaRepository;

    @Mock
    private ConfiguracionParametroRepository parametroRepository;

    @InjectMocks
    private TransaccionServiceImpl transaccionService;

    private CuentaBancaria cuentaOrigen;
    private CuentaBancaria cuentaDestino;
    private UUID cuentaId;

    /**
     * Prepara el estado inicial de dos cuentas con saldos base para testear movimientos de dinero.
     */
    @BeforeEach
    void setUp() {
        cuentaId = UUID.randomUUID();

        // Crear un cliente simulado
        Cliente clientePrueba = new Cliente();
        clientePrueba.setId(UUID.randomUUID());
        clientePrueba.setNombre("Juan");
        clientePrueba.setApellido("Perez");

        // Cuenta de origen con un saldo inicial de $5000
        cuentaOrigen = new CuentaBancaria() {};
        cuentaOrigen.setId(cuentaId);
        cuentaOrigen.setCbu("001");
        cuentaOrigen.setSaldo(BigDecimal.valueOf(5000));
        cuentaOrigen.setEstado(EstadoCuenta.ACTIVA);
        cuentaOrigen.setCliente(clientePrueba);

        // Cuenta de destino con un saldo inicial de $1000
        cuentaDestino = new CuentaBancaria() {};
        cuentaDestino.setId(UUID.randomUUID());
        cuentaDestino.setCbu("002");
        cuentaDestino.setSaldo(BigDecimal.valueOf(1000));
        cuentaDestino.setEstado(EstadoCuenta.ACTIVA);
        cuentaDestino.setCliente(clientePrueba);
    }

    /**
     * Verifica que al depositar $1000, el saldo pase de $5000 a $6000 y se registre la transacción.
     */
    @Test
    void realizarDeposito_Exito() {
        // ARRANGE
        TransaccionRequestDto dto = TransaccionRequestDto.builder()
                .cuentaId(cuentaId)
                .monto(BigDecimal.valueOf(1000))
                .build();

        Transaccion tGuardada = new Transaccion();
        tGuardada.setId(UUID.randomUUID());
        tGuardada.setMonto(dto.getMonto());
        tGuardada.setTipo(TipoTransaccion.DEPOSITO);
        tGuardada.setFecha(LocalDate.now());
        tGuardada.setHora(LocalTime.now());
        tGuardada.setEstadoProcesamiento(EstadoProcesamiento.COMPLETADA);

        when(cuentaRepository.findById(cuentaId)).thenReturn(Optional.of(cuentaOrigen));
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(tGuardada);

        // ACT
        TransaccionResponseDto response = transaccionService.realizarDeposito(dto);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(cuentaOrigen.getSaldo()).isEqualTo(BigDecimal.valueOf(6000)); // 5000 + 1000
        verify(cuentaRepository).save(cuentaOrigen);
    }

    /**
     * Verifica que al extraer $2000, el saldo se reduzca correctamente de $5000 a $3000.
     */
    @Test
    void realizarExtraccion_Exito() {
        // ARRANGE
        TransaccionRequestDto dto = TransaccionRequestDto.builder()
                .cuentaId(cuentaId)
                .monto(BigDecimal.valueOf(2000))
                .build();

        Transaccion tGuardada = new Transaccion();
        tGuardada.setId(UUID.randomUUID());
        tGuardada.setMonto(dto.getMonto());
        tGuardada.setTipo(TipoTransaccion.EXTRACCION);
        tGuardada.setFecha(LocalDate.now());
        tGuardada.setHora(LocalTime.now());

        ConfiguracionParametro parametroMock = ConfiguracionParametro.builder()
                .clave("LIMITE_EXTRACCION")
                .valor("10000")
                .descripcion("Límite diario de extracción")
                .build();

        when(cuentaRepository.findById(cuentaId)).thenReturn(Optional.of(cuentaOrigen));
        when(parametroRepository.findById(any())).thenReturn(Optional.of(parametroMock));

        // MOCK: Simula que no se han registrado extracciones previas en el día
        when(transaccionRepository.sumarExtraccionesDelDia(cuentaId)).thenReturn(BigDecimal.ZERO);

        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(tGuardada);

        // ACT
        TransaccionResponseDto response = transaccionService.realizarExtraccion(dto);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(cuentaOrigen.getSaldo()).isEqualTo(BigDecimal.valueOf(3000)); // 5000 - 2000
    }

    /**
     * Verifica que se lance SaldoInsuficienteException si se intenta extraer más dinero del disponible ($10000 > $5000).
     */
    @Test
    void realizarExtraccion_SaldoInsuficiente() {
        // ARRANGE
        TransaccionRequestDto dto = TransaccionRequestDto.builder()
                .cuentaId(cuentaId)
                .monto(BigDecimal.valueOf(10000))
                .build();

        ConfiguracionParametro parametroMock = ConfiguracionParametro.builder()
                .clave("LIMITE_EXTRACCION")
                .valor("20000")
                .descripcion("Límite diario de extracción")
                .build();

        when(cuentaRepository.findById(cuentaId)).thenReturn(Optional.of(cuentaOrigen));
        when(parametroRepository.findById(any())).thenReturn(Optional.of(parametroMock));

        // MOCK: Evita retornar null al sumar extracciones del día
        when(transaccionRepository.sumarExtraccionesDelDia(cuentaId)).thenReturn(BigDecimal.ZERO);

        // ACT & ASSERT: Se verifica la excepción y que NO se registre ninguna transacción
        assertThatThrownBy(() -> transaccionService.realizarExtraccion(dto))
                .isInstanceOf(SaldoInsuficienteException.class);

        verify(transaccionRepository, never()).save(any());
    }

    /**
     * Verifica el flujo completo de transferencia entre dos cuentas:
     * Resta dinero del origen, suma al destino y guarda ambos comprobantes.
     */
    @Test
    void realizarTransferencia_Exito() {
        // ARRANGE
        TransferenciaRequestDto dto = TransferenciaRequestDto.builder()
                .cbuOrigen("001")
                .cbuDestino("002")
                .monto(BigDecimal.valueOf(2000))
                .build();

        Transaccion tOrigen = new Transaccion();
        tOrigen.setId(UUID.randomUUID());
        tOrigen.setFecha(LocalDate.now());
        tOrigen.setHora(LocalTime.now());

        when(cuentaRepository.findByCbu("001")).thenReturn(Optional.of(cuentaOrigen));
        when(cuentaRepository.findByCbu("002")).thenReturn(Optional.of(cuentaDestino));
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(tOrigen);

        // ACT
        TransferenciaResponseDto response = transaccionService.realizarTransferencia(dto);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getEstado()).isEqualTo(EstadoProcesamiento.COMPLETADA);
        assertThat(cuentaOrigen.getSaldo()).isEqualTo(BigDecimal.valueOf(3000)); // 5000 - 2000
        assertThat(cuentaDestino.getSaldo()).isEqualTo(BigDecimal.valueOf(3000)); // 1000 + 2000
        verify(transaccionRepository, times(2)).save(any(Transaccion.class)); // Se guarda 1 registro para origen y 1 para destino
    }

    /**
     * Verifica que se impida realizar una transferencia cuando el CBU de origen y de destino son idénticos.
     */
    @Test
    void realizarTransferencia_MismoCbuError() {
        // ARRANGE
        TransferenciaRequestDto dto = TransferenciaRequestDto.builder()
                .cbuOrigen("001")
                .cbuDestino("001")
                .monto(BigDecimal.valueOf(1000))
                .build();

        // ACT & ASSERT
        assertThatThrownBy(() -> transaccionService.realizarTransferencia(dto))
                .isInstanceOf(OperacionNoPermitidaException.class);
    }
}