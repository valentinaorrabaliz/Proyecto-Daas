package ar.edu.unju.fi.arquitecturas.tp2banco.servicios.impl;

import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.ClienteResponseDto;
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

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public ClienteResponseDto crearCliente(ClienteRequestDto clienteRequest) {
        if (clienteRepository.existsByCuil(clienteRequest.getCuil())) {
            throw new RecursoDuplicadoException("Ya existe un cliente registrado con el CUIL: " + clienteRequest.getCuil());
        }
        if (clienteRepository.existsByEmail(clienteRequest.getEmail())) {
            throw new RecursoDuplicadoException("Ya existe un cliente registrado con el Email: " + clienteRequest.getEmail());
        }

        Cliente cliente = Cliente.builder()
                .nombre(clienteRequest.getNombre())
                .apellido(clienteRequest.getApellido())
                .dni(clienteRequest.getDni())
                .cuil(clienteRequest.getCuil())
                .email(clienteRequest.getEmail())
                .telefono(clienteRequest.getTelefono())
                .build();

        return mapToResponseDto(clienteRepository.save(cliente));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDto obtenerPorId(UUID id) {
        Cliente cliente = buscarClientePorId(id);
        return mapToResponseDto(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDto obtenerPorCuil(String cuil) {
        Cliente cliente = clienteRepository.findByCuil(cuil)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el cliente con CUIL: " + cuil));
        return mapToResponseDto(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponseDto> listarTodosPaginado(Pageable pageable) {
        return clienteRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDto> buscarPorNombreOApellido(String termino) {
        return clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(termino, termino)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ClienteResponseDto actualizarCliente(UUID id, ClienteRequestDto clienteDetalles) {
        Cliente clienteExistente = buscarClientePorId(id);

        if (!clienteExistente.getEmail().equals(clienteDetalles.getEmail())
                && clienteRepository.existsByEmail(clienteDetalles.getEmail())) {
            throw new RecursoDuplicadoException("El email " + clienteDetalles.getEmail() + " ya está en uso por otro cliente.");
        }

        clienteExistente.setNombre(clienteDetalles.getNombre());
        clienteExistente.setApellido(clienteDetalles.getApellido());
        clienteExistente.setDni(clienteDetalles.getDni());
        clienteExistente.setCuil(clienteDetalles.getCuil());
        clienteExistente.setEmail(clienteDetalles.getEmail());
        clienteExistente.setTelefono(clienteDetalles.getTelefono());

        return mapToResponseDto(clienteRepository.save(clienteExistente));
    }

    @Override
    @Transactional
    public void eliminarCliente(UUID id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No se puede eliminar. No existe el cliente con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private Cliente buscarClientePorId(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el cliente con ID: " + id));
    }

    private ClienteResponseDto mapToResponseDto(Cliente cliente) {
        return ClienteResponseDto.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .dni(cliente.getDni())
                .cuil(cliente.getCuil())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .build();
    }
}