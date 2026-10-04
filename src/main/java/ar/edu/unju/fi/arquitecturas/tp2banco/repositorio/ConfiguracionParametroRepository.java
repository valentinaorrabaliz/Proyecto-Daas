package ar.edu.unju.fi.arquitecturas.tp2banco.repositorio;

import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.ConfiguracionParametro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionParametroRepository extends JpaRepository<ConfiguracionParametro, String> {
}
