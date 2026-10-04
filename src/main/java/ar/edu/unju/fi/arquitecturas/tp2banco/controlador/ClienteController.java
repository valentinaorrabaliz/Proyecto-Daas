package ar.edu.unju.fi.arquitecturas.tp2banco.controlador;

import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.request.VincularAdherenteRequestDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.dto.response.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.tp2banco.servicios.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponseDto> crearCliente(@Valid @RequestBody ClienteRequestDto dto) {
        ClienteResponseDto clienteCreado = clienteService.crearCliente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCreado);
    }

    @PostMapping("/adherentes")
    public ResponseEntity<Void> vincularAdherente(@Valid @RequestBody VincularAdherenteRequestDto request) {
        clienteService.vincularAdherente(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @GetMapping("/cuil/{cuil}")
    public ResponseEntity<ClienteResponseDto> obtenerPorCuil(@PathVariable String cuil) {
        return ResponseEntity.ok(clienteService.obtenerPorCuil(cuil));
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<ClienteResponseDto>> listarTodosPaginado(Pageable pageable) {
        return ResponseEntity.ok(clienteService.listarTodosPaginado(pageable));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponseDto>> buscarPorNombreOApellido(@RequestParam String termino) {
        return ResponseEntity.ok(clienteService.buscarPorNombreOApellido(termino));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> actualizarCliente(@PathVariable UUID id, @Valid @RequestBody ClienteRequestDto dto) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable UUID id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}