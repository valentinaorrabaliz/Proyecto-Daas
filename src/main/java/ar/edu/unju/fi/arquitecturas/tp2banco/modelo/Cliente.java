package ar.edu.unju.fi.arquitecturas.tp2banco.modelo;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCliente;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.RolFamiliar;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa a un Cliente del banco en la base de datos.
 *
 * Hereda de Auditable para registrar automaticamente las fechas de creacion
 * y modificacion.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "clientes")
public class Cliente extends Auditable {

    /**
     * Identificador del cliente utilizando UUID.
     * Esto evita que los IDs sean predecibles o vulnerables a enumeracion.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Nombres del cliente.
     */
    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    /**
     * Apellidos del cliente.
     */
    @Column(name = "apellido", nullable = false, length = 60)
    private String apellido;

    /**
     * DNI argentino (unico y de 8 caracteres).
     */
    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    /**
     * cuil sin guiones (11 dígitos).
     */
    @Column(name = "cuil", nullable = false, unique = true, length = 11)
    private String cuil;

    /**
     * Correo electronico del cliente.
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Numero de telefono del cliente.
     */
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;


    @Enumerated(EnumType.STRING)
    @Column(name = "rol_familiar")
    private RolFamiliar rolFamiliar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titular_id")
    private Cliente titular; // Nulo si este cliente es el Titular

    @OneToMany(mappedBy = "titular", cascade = CascadeType.ALL)
    private List<Cliente> adherentes; // Lista de adherentes vinculados

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cliente", nullable = false)
    private EstadoCliente estadoCliente;

    @Column(name = "token_activacion")
    private String tokenActivacion;

    @Column(name = "fecha_expiracion_token")
    private LocalDateTime fechaExpiracionToken;
}
