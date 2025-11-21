package UTP.Zum.Persistencia.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import UTP.Zum.Model.Rol;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombreRol(String nombreRol);
}