package ar.edu.unju.fi.arquitecturas.tp2banco.servicios.impl;

import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.RecursoDuplicadoException;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.tp2banco.servicios.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    // Inyección de dependencias por constructor (Buena práctica)
    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public Cliente crearCliente(Cliente cliente) {
        // Validación con existsByCuilOrEmail para evitar duplicados
        if (clienteRepository.existsByCuil(cliente.getCuil())) {
            throw new RecursoDuplicadoException("Ya existe un cliente registrado con el CUIL: " + cliente.getCuil());
        }
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new RecursoDuplicadoException("Ya existe un cliente registrado con el Email: " + cliente.getEmail());
        }
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerPorId(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el cliente con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerPorCuil(String cuil) {
        return clienteRepository.findByCuil(cuil)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el cliente con CUIL: " + cuil));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Cliente> listarTodosPaginado(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNombreOApellido(String termino) {
        return clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(termino, termino);
    }

    @Override
    @Transactional
    public Cliente actualizarCliente(UUID id, Cliente clienteDetalles) {
        Cliente clienteExistente = obtenerPorId(id);

        // Validar si el nuevo email ya pertenece a otro cliente
        if (!clienteExistente.getEmail().equals(clienteDetalles.getEmail())
                && clienteRepository.existsByEmail(clienteDetalles.getEmail())) {
            throw new RecursoDuplicadoException("El email " + clienteDetalles.getEmail() + " ya está en uso por otro cliente.");
        }

        clienteExistente.setNombre(clienteDetalles.getNombre());
        clienteExistente.setApellido(clienteDetalles.getApellido());
        clienteExistente.setEmail(clienteDetalles.getEmail());
        clienteExistente.setTelefono(clienteDetalles.getTelefono());

        return clienteRepository.save(clienteExistente);
    }


    @Override
    @Transactional
    public void eliminarCliente(UUID id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No se puede eliminar. No existe el cliente con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }
}