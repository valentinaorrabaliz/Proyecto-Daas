package ar.edu.unju.fi.arquitecturas.tp2banco.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre no puede superar los 60 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60, message = "El apellido no puede superar los 60 caracteres")
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 7, max = 8, message = "El DNI debe tener entre 7 y 8 caracteres")
    private String dni;

    @NotBlank(message = "El CUIL es obligatorio")
    @Size(min = 11, max = 11, message = "El CUIL debe tener exactamente 11 dígitos")
    private String cuil;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ingresar un correo electrónico válido")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telefono;
}