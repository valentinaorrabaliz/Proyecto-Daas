package ar.edu.unju.fi.arquitecturas.tp2banco.repositorio;

import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de acceso a datos para la entidad Cliente.
 * Extiende de JpaRepository para obtener automáticamente los métodos CRUD básicos
 * (save, findById, findAll, deleteById, etc.) sin necesidad de implementar SQL a mano.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {


    /**
     * Busca un cliente por su número de CUIL.
     * Retorna un Optional para manejar de forma segura la presencia o ausencia del registro.
     */
    Optional<Cliente> findByCuil(String cuil);


    /**
     * Busca un cliente por su email (búsqueda exacta).
     */
    Optional<Cliente> findByEmail(String email);


    /**
     * Verifica la existencia de un cliente a partir de su CUIL.
     * Es más eficiente que realizar un findByCuil completo cuando solo se requiere validar duplicados.
     */
    boolean existsByCuil(String cuil);


    /**
     * Verifica si existe un cliente registrado con el email especificado.
     */
    boolean existsByEmail(String email);


    /**
     * Verifica si existe un cliente con el CUIL o el Email especificados.
     */
    boolean existsByCuilOrEmail(String cuil, String email);


    /**
     * Busca clientes cuyo nombre o apellido contenga la cadena proporcionada,
     * ignorando mayúsculas y minúsculas.
     */
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);


    /**
     * Busca clientes cuyos apellidos coincidan parcialmente con la cadena buscada,
     * ignorando mayúsculas y minúsculas.
     */
    List<Cliente> findByApellidoContainingIgnoreCase(String apellido);

    /**
     * Busca clientes cuyo nombre O apellido contenga la cadena buscada..
     */
    List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    /**
     * Busca un cliente a partir de su token de activación.
     */
    Optional<Cliente> findByTokenActivacion(String token);
}