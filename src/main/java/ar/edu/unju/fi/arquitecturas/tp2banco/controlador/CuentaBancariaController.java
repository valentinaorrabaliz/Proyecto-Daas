package ar.edu.unju.fi.arquitecturas.tp2banco.controlador;

import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.CuentaBancariaRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.CuentaBancariaResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.servicios.CuentaBancariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
public class CuentaBancariaController {

    private final CuentaBancariaService cuentaService;

    @PostMapping
    public ResponseEntity<CuentaBancariaResponseDto> crearCuenta(@Valid @RequestBody CuentaBancariaRequestDto dto) {
        CuentaBancariaResponseDto cuentaCreada = cuentaService.crearCuenta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaCreada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaBancariaResponseDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(cuentaService.obtenerPorId(id));
    }

    @GetMapping("/cbu/{cbu}")
    public ResponseEntity<CuentaBancariaResponseDto> obtenerPorCbu(@PathVariable String cbu) {
        return ResponseEntity.ok(cuentaService.obtenerPorCbu(cbu));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CuentaBancariaResponseDto>> obtenerCuentasPorCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(cuentaService.obtenerCuentasPorCliente(clienteId));
    }
}