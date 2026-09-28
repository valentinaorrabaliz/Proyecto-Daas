package ar.edu.unju.fi.arquitecturas.tp2banco.servicios.impl;

import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.RecursoDuplicadoException;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la implementación del servicio de Clientes (ClienteServiceImpl).
 * Utiliza Mockito para simular la capa de persistencia (ClienteRepository).
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    // Simulación del repositorio de clientes (Evita conectarse a la base de datos real)
    @Mock
    private ClienteRepository clienteRepository;

    // Inyección de los mocks en la instancia real del servicio a probar
    @InjectMocks
    private ClienteServiceImpl clienteService;

    // Variables de prueba globales para ser reutilizadas en cada test
    private Cliente cliente;
    private ClienteRequestDto clienteRequestDto;
    private UUID clienteId;

    /**
     * Configuración previa a la ejecución de cada prueba individual.
     * Prepara objetos con datos de prueba comunes.
     */
    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();

        cliente = Cliente.builder()
                .id(clienteId)
                .nombre("Juan")
                .apellido("Pérez")
                .dni("12345678")
                .cuil("20123456789")
                .email("juan.perez@example.com")
                .telefono("3881234567")
                .build();

        clienteRequestDto = ClienteRequestDto.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .dni("12345678")
                .cuil("20123456789")
                .email("juan.perez@example.com")
                .telefono("3881234567")
                .build();
    }

    /**
     * Verifica la creación exitosa de un cliente cuando las validaciones de CUIL y Email son correctas.
     */
    @Test
    void crearCliente_Exito() {
        // ARRANGE (Preparar): Definir el comportamiento esperado de los mocks
        when(clienteRepository.existsByCuil(clienteRequestDto.getCuil())).thenReturn(false);
        when(clienteRepository.existsByEmail(clienteRequestDto.getEmail())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // ACT (Ejecutar): Llamar al método del servicio
        ClienteResponseDto response = clienteService.crearCliente(clienteRequestDto);

        // ASSERT (Verificar): Comprobar que los resultados concuerden con lo esperado
        assertThat(response).isNotNull();
        assertThat(response.getCuil()).isEqualTo(clienteRequestDto.getCuil());
        assertThat(response.getEmail()).isEqualTo(clienteRequestDto.getEmail());
        verify(clienteRepository).save(any(Cliente.class)); // Confirma que se llamó al guardar
    }

    /**
     * Verifica que se lance una excepción cuando se intenta crear un cliente con un CUIL ya registrado.
     */
    @Test
    void crearCliente_ErrorCuilDuplicado() {
        // ARRANGE: El repositorio indica que el CUIL ya existe
        when(clienteRepository.existsByCuil(clienteRequestDto.getCuil())).thenReturn(true);

        // ACT & ASSERT: Verificar que se arroje la excepción y que no se guarde nada
        assertThatThrownBy(() -> clienteService.crearCliente(clienteRequestDto))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("CUIL");

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /**
     * Verifica que se lance una excepción cuando el CUIL es único pero el Email ya está registrado.
     */
    @Test
    void crearCliente_ErrorEmailDuplicado() {
        // ARRANGE: CUIL válido, pero Email ya ocupado
        when(clienteRepository.existsByCuil(clienteRequestDto.getCuil())).thenReturn(false);
        when(clienteRepository.existsByEmail(clienteRequestDto.getEmail())).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.crearCliente(clienteRequestDto))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Email");

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /**
     * Verifica que se retorne un cliente correctamente cuando se busca por un ID existente.
     */
    @Test
    void obtenerPorId_Exito() {
        // ARRANGE
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

        // ACT
        ClienteResponseDto response = clienteService.obtenerPorId(clienteId);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(clienteId);
    }

    /**
     * Verifica que se lance RecursoNoEncontradoException cuando se busca por un ID inexistente.
     */
    @Test
    void obtenerPorId_NoEncontrado() {
        // ARRANGE
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.obtenerPorId(clienteId))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    /**
     * Verifica la búsqueda exitosa de un cliente a través de su CUIL.
     */
    @Test
    void obtenerPorCuil_Exito() {
        // ARRANGE
        when(clienteRepository.findByCuil("20123456789")).thenReturn(Optional.of(cliente));

        // ACT
        ClienteResponseDto response = clienteService.obtenerPorCuil("20123456789");

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getCuil()).isEqualTo("20123456789");
    }

    /**
     * Verifica que la consulta paginada de clientes devuelva una estructura de página válida.
     */
    @Test
    void listarTodosPaginado_Exito() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> page = new PageImpl<>(List.of(cliente));
        when(clienteRepository.findAll(pageable)).thenReturn(page);

        // ACT
        Page<ClienteResponseDto> response = clienteService.listarTodosPaginado(pageable);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    /**
     * Verifica la búsqueda de clientes por coincidencias parciales en nombre o apellido.
     */
    @Test
    void buscarPorNombreOApellido_Exito() {
        // ARRANGE
        when(clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase("Juan", "Juan"))
                .thenReturn(List.of(cliente));

        // ACT
        List<ClienteResponseDto> response = clienteService.buscarPorNombreOApellido("Juan");

        // ASSERT
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getNombre()).isEqualTo("Juan");
    }

    /**
     * Verifica la actualización exitosa de los datos de un cliente existente.
     */
    @Test
    void actualizarCliente_Exito() {
        // ARRANGE
        ClienteRequestDto updateDto = ClienteRequestDto.builder()
                .nombre("Juan Carlos")
                .apellido("Pérez")
                .dni("12345678")
                .cuil("20123456789")
                .email("juan.perez@example.com")
                .telefono("3889999999")
                .build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // ACT
        ClienteResponseDto response = clienteService.actualizarCliente(clienteId, updateDto);

        // ASSERT
        assertThat(response).isNotNull();
        verify(clienteRepository).save(cliente);
    }

    /**
     * Verifica que no se permita actualizar el email de un cliente si este pertenece a otro registro.
     */
    @Test
    void actualizarCliente_ErrorEmailDuplicado() {
        // ARRANGE
        ClienteRequestDto updateDto = ClienteRequestDto.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .dni("12345678")
                .cuil("20123456789")
                .email("nuevo.email@example.com")
                .build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByEmail("nuevo.email@example.com")).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.actualizarCliente(clienteId, updateDto))
                .isInstanceOf(RecursoDuplicadoException.class);

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /**
     * Verifica la eliminación física de un cliente cuando el ID existe en el sistema.
     */
    @Test
    void eliminarCliente_Exito() {
        // ARRANGE
        when(clienteRepository.existsById(clienteId)).thenReturn(true);

        // ACT
        clienteService.eliminarCliente(clienteId);

        // ASSERT
        verify(clienteRepository).deleteById(clienteId);
    }

    /**
     * Verifica que se lance una excepción si se intenta eliminar un cliente inexistente.
     */
    @Test
    void eliminarCliente_NoEncontrado() {
        // ARRANGE
        when(clienteRepository.existsById(clienteId)).thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> clienteService.eliminarCliente(clienteId))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(clienteRepository, never()).deleteById(any());
    }
}