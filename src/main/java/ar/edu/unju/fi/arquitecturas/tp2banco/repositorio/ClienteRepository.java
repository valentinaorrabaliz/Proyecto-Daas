package ar.edu.unju.fi.arquitecturas.tp2banco.repositorio;


import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Query Method 1
    Optional<Cliente> findByCuil(Long cuil);                     // <-- Part 3

    // Query Method 2
    List<Cliente> findByNombreContainingIgnoreCase(String nombre); // <-- Part 4
}
