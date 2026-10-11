package ar.edu.unju.fi.arquitecturas.tp2banco.modelo;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCliente;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.RolFamiliar;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa a un Cliente del banco en la base de datos.
 * Hereda de Auditable para registrar automaticamente las fechas de creacion y modificacion.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "clientes")
public class Cliente extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 60)
    private String apellido;

    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "cuil", nullable = false, unique = true, length = 11)
    private String cuil;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_familiar")
    private RolFamiliar rolFamiliar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titular_id")
    private Cliente titular; // Nulo si este cliente es el Titular

    @OneToMany(mappedBy = "titular", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Cliente> adherentes = new ArrayList<>(); // Lista de adherentes vinculados

    // Relación bidireccional con CuentaBancaria
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CuentaBancaria> cuentas = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cliente", nullable = false)
    private EstadoCliente estadoCliente;

    @Column(name = "token_activacion")
    private String tokenActivacion;

    @Column(name = "fecha_expiracion_token")
    private LocalDateTime fechaExpiracionToken;
}