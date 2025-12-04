package UTP.Zum.Persistencia.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import UTP.Zum.Model.Reunion;
import UTP.Zum.Model.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReunionRepository extends JpaRepository<Reunion, Integer> {
    List<Reunion> findByCreador(Usuario creador);
    
    List<Reunion> findByCreadorOrderByFechaDeInicioDesc(Usuario creador);
    
    //reuniones activas
    @Query("SELECT r FROM Reunion r WHERE r.fechaDeInicio >= :fecha OR r.activa = true ORDER BY r.fechaDeInicio ASC")
    List<Reunion> findReunionesDisponibles(@Param("fecha") LocalDateTime fecha);
    
    List<Reunion> findAllByOrderByFechaDeInicioDesc();
    Optional<Reunion> findByCodigoAcceso(String codigoAcceso);
    List<Reunion> findByActivaTrue();
    List<Reunion> findByGrabacionHabilitadaTrue();
}
