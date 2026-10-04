package ar.edu.unju.fi.arquitecturas.tp2banco.evento;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ClienteCreadoEvent {
    private final UUID clienteId;
    private final String nombre;
    private final String email;
    private final String tokenActivacion;
}