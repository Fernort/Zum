package UTP.Zum.Services;

import UTP.Zum.Dto.RegistroRequest;
import UTP.Zum.Model.Usuario;

public interface UsuarioService {
    Usuario registrarUsuario(RegistroRequest registroRequest) throws Exception;
}
