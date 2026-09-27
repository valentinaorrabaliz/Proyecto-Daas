package ar.edu.unju.fi.arquitecturas.tp2banco.servicios;

import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.ClienteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz que define la lógica de negocio para la gestión de Clientes.
 */
public interface ClienteService {

    ClienteResponseDto crearCliente(ClienteRequestDto clienteRequest);

    ClienteResponseDto obtenerPorId(UUID id);

    ClienteResponseDto obtenerPorCuil(String cuil);

    /**
     * Paginación para evitar la carga masiva de datos en memoria RAM.
     */
    Page<ClienteResponseDto> listarTodosPaginado(Pageable pageable);

    List<ClienteResponseDto> buscarPorNombreOApellido(String termino);

    ClienteResponseDto actualizarCliente(UUID id, ClienteRequestDto clienteDetalles);

    void eliminarCliente(UUID id);
}