package ar.edu.unju.fi.arquitecturas.tp2banco.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponseDto {

    private UUID id;
    private String nombre;
    private String apellido;
    private String dni;
    private String cuil;
    private String email;
    private String telefono;
}