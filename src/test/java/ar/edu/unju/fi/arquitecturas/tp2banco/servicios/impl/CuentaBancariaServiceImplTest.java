package ar.edu.unju.fi.arquitecturas.tp2banco.servicios.impl;

import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.CuentaBancariaRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.CuentaBancariaResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.RecursoDuplicadoException;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.CuentaBancariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la gestión de Cuentas Bancarias..
 * Simula las dependencias de CuentaBancariaRepository y ClienteRepository.
 */

@ExtendWith(MockitoExtension.class)
class CuentaBancariaServiceImplTest {

    @Mock
    private CuentaBancariaRepository cuentaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private CuentaBancariaServiceImpl cuentaService;

    private Cliente cliente;
    private CuentaBancaria cuenta;
    private CuentaBancariaRequestDto requestDto;
    private UUID clienteId;
    private UUID cuentaId;

    /**
     * Inicializa las entidades necesarias para probar la creación y modificación de cuentas.
     */

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        cuentaId = UUID.randomUUID();

        cliente = Cliente.builder().id(clienteId).nombre("Pedro").build();

        cuenta = new CuentaBancaria() {};
        cuenta.setId(cuentaId);
        cuenta.setCbu("0000003100000000000001");
        cuenta.setAlias("ALIAS.PRUEBA");
        cuenta.setSaldo(BigDecimal.valueOf(1000));
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setCliente(cliente);

        requestDto = CuentaBancariaRequestDto.builder()
                .clienteId(clienteId)
                .cbu("0000003100000000000001")
                .alias("ALIAS.PRUEBA")
                .saldoInicial(BigDecimal.valueOf(1000))
                .build();
    }

    /**
     * Verifica la apertura de una nueva cuenta bancaria asociada a un cliente válido.
     */
    @Test
    void crearCuenta_Exito() {
        // ARRANGE: El cliente existe y el CBU no está registrado
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(cuentaRepository.existsByCbu(requestDto.getCbu())).thenReturn(false);
        when(cuentaRepository.save(any(CuentaBancaria.class))).thenReturn(cuenta);

        // ACT
        CuentaBancariaResponseDto response = cuentaService.crearCuenta(requestDto);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getCbu()).isEqualTo(requestDto.getCbu());
        assertThat(response.getEstado()).isEqualTo(EstadoCuenta.ACTIVA);
    }

    /**
     * Verifica que falle la creación si el cliente asociado no existe.
     */
    @Test
    void crearCuenta_ClienteNoEncontrado() {
        // ARRANGE
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> cuentaService.crearCuenta(requestDto))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    /**
     * Verifica que no se cree la cuenta si el CBU asignado ya pertenece a otra cuenta.
     */
    @Test
    void crearCuenta_CbuDuplicado() {
        // ARRANGE
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(cuentaRepository.existsByCbu(requestDto.getCbu())).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> cuentaService.crearCuenta(requestDto))
                .isInstanceOf(RecursoDuplicadoException.class);
    }

    /**
     * Búsqueda exitosa de una cuenta a partir de su CBU de 22 dígitos.
     */
    @Test
    void obtenerPorCbu_Exito() {
        // ARRANGE
        when(cuentaRepository.findByCbu("0000003100000000000001")).thenReturn(Optional.of(cuenta));

        // ACT
        CuentaBancariaResponseDto response = cuentaService.obtenerPorCbu("0000003100000000000001");

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getCbu()).isEqualTo("0000003100000000000001");
    }

    /**
     * Verifica la obtención de todas las cuentas pertenecientes a un titular específico.
     */
    @Test
    void obtenerCuentasPorCliente_Exito() {
        // ARRANGE
        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        when(cuentaRepository.findByClienteId(clienteId)).thenReturn(List.of(cuenta));

        // ACT
        List<CuentaBancariaResponseDto> list = cuentaService.obtenerCuentasPorCliente(clienteId);

        // ASSERT
        assertThat(list).hasSize(1);
    }

    /**
     * Verifica el cambio de estado de la cuenta (ej. de ACTIVA a SUSPENDIDA).
     */
    @Test
    void cambiarEstadoCuenta_Exito() {
        // ARRANGE
        when(cuentaRepository.findById(cuentaId)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(CuentaBancaria.class))).thenReturn(cuenta);

        // ACT
        CuentaBancariaResponseDto response = cuentaService.cambiarEstadoCuenta(cuentaId, EstadoCuenta.SUSPENDIDA);

        // ASSERT
        assertThat(response).isNotNull();
        verify(cuentaRepository).save(cuenta);
    }
}