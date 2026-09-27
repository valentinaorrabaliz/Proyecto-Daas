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
import ar.edu.unju.fi.arquitecturas.tp2banco.servicios.CuentaBancariaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CuentaBancariaServiceImpl implements CuentaBancariaService {

    private final CuentaBancariaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    public CuentaBancariaServiceImpl(CuentaBancariaRepository cuentaRepository, ClienteRepository clienteRepository) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public CuentaBancariaResponseDto crearCuenta(CuentaBancariaRequestDto cuentaRequest) {
        Cliente cliente = clienteRepository.findById(cuentaRequest.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se puede crear la cuenta. El cliente titular no existe."));

        if (cuentaRepository.existsByCbu(cuentaRequest.getCbu())) {
            throw new RecursoDuplicadoException("El CBU " + cuentaRequest.getCbu() + " ya se encuentra registrado.");
        }

        CuentaBancaria cuenta = new CuentaBancaria() {};
        cuenta.setCbu(cuentaRequest.getCbu());
        cuenta.setAlias(cuentaRequest.getAlias());
        cuenta.setSaldo(cuentaRequest.getSaldoInicial() != null ? cuentaRequest.getSaldoInicial() : BigDecimal.ZERO);
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setCliente(cliente);

        return mapToResponseDto(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaBancariaResponseDto obtenerPorId(UUID id) {
        CuentaBancaria cuenta = buscarCuentaPorId(id);
        return mapToResponseDto(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaBancariaResponseDto obtenerPorCbu(String cbu) {
        CuentaBancaria cuenta = cuentaRepository.findByCbu(cbu)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la cuenta bancaria asociada al CBU: " + cbu));
        return mapToResponseDto(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaBancariaResponseDto> obtenerCuentasPorCliente(UUID clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNoEncontradoException("El cliente especificado no existe.");
        }
        return cuentaRepository.findByClienteId(clienteId)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CuentaBancariaResponseDto> listarPorEstado(EstadoCuenta estado, Pageable pageable) {
        return cuentaRepository.findByEstado(estado, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public CuentaBancariaResponseDto cambiarEstadoCuenta(UUID id, EstadoCuenta nuevoEstado) {
        CuentaBancaria cuenta = buscarCuentaPorId(id);
        cuenta.setEstado(nuevoEstado);
        return mapToResponseDto(cuentaRepository.save(cuenta));
    }

    private CuentaBancaria buscarCuentaPorId(UUID id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la cuenta bancaria con ID: " + id));
    }

    private CuentaBancariaResponseDto mapToResponseDto(CuentaBancaria cuenta) {
        return CuentaBancariaResponseDto.builder()
                .id(cuenta.getId())
                .cbu(cuenta.getCbu())
                .alias(cuenta.getAlias())
                .saldo(cuenta.getSaldo())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getCliente() != null ? cuenta.getCliente().getId() : null)
                .build();
    }
}