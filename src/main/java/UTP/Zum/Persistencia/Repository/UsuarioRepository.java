package UTP.Zum.Persistencia.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import UTP.Zum.Model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreoUsuario(String correoUsuario);
    boolean existsByCorreoUsuario(String correoUsuario);
}