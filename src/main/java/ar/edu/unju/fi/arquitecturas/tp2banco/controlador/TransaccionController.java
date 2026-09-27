package ar.edu.unju.fi.arquitecturas.tp2banco.controlador;

import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.TransaccionRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.TransferenciaRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.TransaccionResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.TransferenciaResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2banco.servicios.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;

    @PostMapping("/deposito")
    public ResponseEntity<TransaccionResponseDto> realizarDeposito(@Valid @RequestBody TransaccionRequestDto dto) {
        TransaccionResponseDto respuesta = transaccionService.realizarDeposito(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PostMapping("/extraccion")
    public ResponseEntity<TransaccionResponseDto> realizarExtraccion(@Valid @RequestBody TransaccionRequestDto dto) {
        TransaccionResponseDto respuesta = transaccionService.realizarExtraccion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PostMapping("/transferir")
    public ResponseEntity<TransferenciaResponseDto> realizarTransferencia(@Valid @RequestBody TransferenciaRequestDto dto) {
        TransferenciaResponseDto respuesta = transaccionService.realizarTransferencia(dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionResponseDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(transaccionService.obtenerPorId(id));
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<TransaccionResponseDto>> obtenerHistorialCuenta(@PathVariable UUID cuentaId) {
        return ResponseEntity.ok(transaccionService.obtenerHistorialCuenta(cuentaId));
    }

    @GetMapping("/cuenta/{cuentaId}/fechas")
    public ResponseEntity<List<TransaccionResponseDto>> obtenerHistorialCuentaPorFechas(
            @PathVariable UUID cuentaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(transaccionService.obtenerHistorialCuentaPorFechas(cuentaId, inicio, fin));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Page<TransaccionResponseDto>> listarPorTipo(
            @PathVariable TipoTransaccion tipo,
            Pageable pageable) {
        return ResponseEntity.ok(transaccionService.listarPorTipo(tipo, pageable));
    }
}