package UTP.Zum.Services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import UTP.Zum.Model.Reunion;
import UTP.Zum.Model.Usuario;
import UTP.Zum.Persistencia.Repository.ReunionRepository;
import UTP.Zum.Services.ReunionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReunionImpl implements ReunionService {

    @Autowired
    private ReunionRepository reunionRepository;

    @Override
    public Reunion crearReunion(Reunion reunion, Usuario creador) {
        reunion.setCreador(creador);
        reunion.setActiva(false);
        reunion.setEstado("PROGRAMADA");
        reunion.setGrabacionHabilitada(false);
        return reunionRepository.save(reunion);
    }

    @Override
    public List<Reunion> obtenerReunionesProximas() {
        // Obtiene reuniones activas
        return reunionRepository.findReunionesDisponibles(LocalDateTime.now());
    }

    @Override
    public List<Reunion> obtenerReunionesDelUsuario(Usuario usuario) {
        return reunionRepository.findByCreadorOrderByFechaDeInicioDesc(usuario);
    }

    @Override
    public List<Reunion> obtenerTodasLasReuniones() {
        //Para el alumno
        return reunionRepository.findAllByOrderByFechaDeInicioDesc();
    }

    @Override
    public Optional<Reunion> buscarPorCodigo(String codigo) {
        return reunionRepository.findByCodigoAcceso(codigo);
    }

    @Override
    public Optional<Reunion> buscarPorId(Integer id) {
        return reunionRepository.findById(id);
    }

    @Override
    public void iniciarReunion(Integer reunionId) {
        reunionRepository.findById(reunionId).ifPresent(reunion -> {
            reunion.setActiva(true);
            reunion.setEstado("EN_CURSO");
            reunionRepository.save(reunion);
        });
    }

    @Override
    public void finalizarReunion(Integer reunionId) {
        reunionRepository.findById(reunionId).ifPresent(reunion -> {
            reunion.setActiva(false);
            reunion.setEstado("FINALIZADA");
            reunionRepository.save(reunion);
        });
    }

    @Override
    public void habilitarGrabacion(Integer reunionId) {
        reunionRepository.findById(reunionId).ifPresent(reunion -> {
            reunion.setGrabacionHabilitada(true);
            reunionRepository.save(reunion);
        });
    }

    @Override
    public void eliminarReunion(Integer reunionId) {
        reunionRepository.deleteById(reunionId);
    }

    @Override
    public List<Reunion> obtenerGrabaciones() {
        return reunionRepository.findByGrabacionHabilitadaTrue();
    }
}
