package UTP.Zum.Persistencia.Repository;


import UTP.Zum.Persistencia.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreoUsuario(String correoUsuario);
    boolean existsByCorreoUsuario(String correoUsuario);
}