package ar.edu.unju.fi.arquitecturas.tp2banco.servicios;

import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz que define la lógica de negocio para la gestión de Clientes.
 */

public interface ClienteService {

    Cliente crearCliente(Cliente cliente);

    Cliente obtenerPorId(UUID id);

    Cliente obtenerPorCuil(String cuil);

    /**
     * Paginación para evitar la carga masiva de datos en memoria RAM.
     */
    Page<Cliente> listarTodosPaginado(Pageable pageable);

    List<Cliente> buscarPorNombreOApellido(String termino);

    Cliente actualizarCliente(UUID id, Cliente clienteDetalles);

    @Transactional
    void eliminarCliente(UUID id);
}