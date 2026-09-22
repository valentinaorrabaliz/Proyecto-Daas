package ar.edu.unju.fi.arquitecturas.tp2banco.servicios.impl;

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
    public CuentaBancaria crearCuenta(CuentaBancaria cuenta, UUID clienteId) {
        // Validar existencia del titular
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se puede crear la cuenta. El cliente titular no existe."));

        // Validar CBU duplicado
        if (cuentaRepository.existsByCbu(cuenta.getCbu())) {
            throw new RecursoDuplicadoException("El CBU " + cuenta.getCbu() + " ya se encuentra registrado.");
        }

        cuenta.setCliente(cliente);
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaBancaria obtenerPorId(UUID id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la cuenta bancaria con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaBancaria obtenerPorCbu(String cbu) {
        return cuentaRepository.findByCbu(cbu)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la cuenta bancaria asociada al CBU: " + cbu));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaBancaria> obtenerCuentasPorCliente(UUID clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNoEncontradoException("El cliente especificado no existe.");
        }
        return cuentaRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CuentaBancaria> listarPorEstado(EstadoCuenta estado, Pageable pageable) {
        return cuentaRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public CuentaBancaria cambiarEstadoCuenta(UUID id, EstadoCuenta nuevoEstado) {
        CuentaBancaria cuenta = obtenerPorId(id);
        cuenta.setEstado(nuevoEstado);
        return cuentaRepository.save(cuenta);
    }
}