package UTP.Zum.Services;

import UTP.Zum.Model.Reunion;
import UTP.Zum.Model.Usuario;

import java.util.List;
import java.util.Optional;

public interface ReunionService {
    
    Reunion crearReunion(Reunion reunion, Usuario creador);
    
    List<Reunion> obtenerReunionesProximas();
    
    List<Reunion> obtenerReunionesDelUsuario(Usuario usuario);
    
    List<Reunion> obtenerTodasLasReuniones();
    
    Optional<Reunion> buscarPorCodigo(String codigo);
    
    Optional<Reunion> buscarPorId(Integer id);
    
    void iniciarReunion(Integer reunionId);
    
    void finalizarReunion(Integer reunionId);
    
    void habilitarGrabacion(Integer reunionId);
    
    void eliminarReunion(Integer reunionId);
    
    List<Reunion> obtenerGrabaciones();
}
