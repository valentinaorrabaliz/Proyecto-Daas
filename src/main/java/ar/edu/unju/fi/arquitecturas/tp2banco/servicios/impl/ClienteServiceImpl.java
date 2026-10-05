package ar.edu.unju.fi.arquitecturas.tp2banco.servicios.impl;

import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.VincularAdherenteRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCliente;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.RolFamiliar;
import ar.edu.unju.fi.arquitecturas.tp2banco.evento.ClienteCreadoEvent;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.OperacionNoPermitidaException;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.RecursoDuplicadoException;
import ar.edu.unju.fi.arquitecturas.tp2banco.excepcion.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.tp2banco.servicios.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ClienteResponseDto crearCliente(ClienteRequestDto clienteRequest) {
        if (clienteRepository.existsByCuil(clienteRequest.getCuil())) {
            throw new RecursoDuplicadoException("Ya existe un cliente registrado con el CUIL: " + clienteRequest.getCuil());
        }
        if (clienteRepository.existsByEmail(clienteRequest.getEmail())) {
            throw new RecursoDuplicadoException("Ya existe un cliente registrado con el Email: " + clienteRequest.getEmail());
        }

        String token = UUID.randomUUID().toString();

        Cliente cliente = Cliente.builder()
                .nombre(clienteRequest.getNombre())
                .apellido(clienteRequest.getApellido())
                .dni(clienteRequest.getDni())
                .cuil(clienteRequest.getCuil())
                .email(clienteRequest.getEmail())
                .telefono(clienteRequest.getTelefono())
                .estadoCliente(EstadoCliente.PENDIENTE_ACTIVACION)
                .tokenActivacion(token)
                .fechaExpiracionToken(LocalDateTime.now().plusHours(24))
                .build();

        Cliente clienteGuardado = clienteRepository.save(cliente);

        // Publicar evento para envio asincrono del correo de activacion
        eventPublisher.publishEvent(new ClienteCreadoEvent(
                clienteGuardado.getId(),
                clienteGuardado.getNombre(),
                clienteGuardado.getEmail(),
                clienteGuardado.getTokenActivacion()
        ));

        return mapToResponseDto(clienteGuardado);
    }

    @Override
    @Transactional
    public void activarCuenta(String token) {
        // Correcto: si no encuentra el token, lanza 404 Not Found
        Cliente cliente = clienteRepository.findByTokenActivacion(token)
                .orElseThrow(() -> new RecursoNoEncontradoException("El token de activación provisto es inválido."));

        if (cliente.getEstadoCliente() == EstadoCliente.ACTIVO) {
            // CAMBIO: OperacionNoPermitidaException
            throw new OperacionNoPermitidaException("El token ya fue utilizado. La cuenta ya se encuentra ACTIVA.");
        }

        if (cliente.getFechaExpiracionToken() != null && cliente.getFechaExpiracionToken().isBefore(LocalDateTime.now())) {
            // CAMBIO: OperacionNoPermitidaException
            throw new OperacionNoPermitidaException("El token de activación ha expirado.");
        }

        cliente.setEstadoCliente(EstadoCliente.ACTIVO);
        cliente.setTokenActivacion(null);
        cliente.setFechaExpiracionToken(null);

        clienteRepository.save(cliente);
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

    public void vincularAdherente(VincularAdherenteRequestDto request) {
        if (request.getRolFamiliar() == RolFamiliar.TITULAR) {
            // CAMBIO: OperacionNoPermitidaException
            throw new OperacionNoPermitidaException("No se puede vincular un adherente con el rol TITULAR");
        }

        Cliente titular = buscarClientePorId(request.getTitularId());
        Cliente adherente = buscarClientePorId(request.getAdherenteId());

        if (adherente.getTitular() != null) {
            // CAMBIO: OperacionNoPermitidaException
            throw new OperacionNoPermitidaException("El cliente ingresado como adherente ya pertenece a un grupo familiar");
        }

        if (titular.getTitular() != null) {
            // CAMBIO: OperacionNoPermitidaException
            throw new OperacionNoPermitidaException("Un cliente que es adherente no puede ser titular de otro grupo familiar");
        }

        if (titular.getRolFamiliar() == null) {
            titular.setRolFamiliar(RolFamiliar.TITULAR);
            clienteRepository.save(titular);
        }

        adherente.setTitular(titular);
        adherente.setRolFamiliar(request.getRolFamiliar());
        clienteRepository.save(adherente);
    }
}