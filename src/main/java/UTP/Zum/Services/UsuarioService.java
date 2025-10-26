package UTP.Zum.Services;

import UTP.Zum.Dto.RegistroRequest;
import UTP.Zum.Persistencia.Usuario;

public interface UsuarioService {
    Usuario registrarUsuario(RegistroRequest registroRequest) throws Exception;
}
