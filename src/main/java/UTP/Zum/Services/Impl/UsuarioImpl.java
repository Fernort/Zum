package UTP.Zum.Services.Impl;

import UTP.Zum.Services.UsuarioService;
import UTP.Zum.Persistencia.Rol;
import UTP.Zum.Persistencia.Usuario;
import UTP.Zum.Persistencia.Repository.RolRepository;
import UTP.Zum.Persistencia.Repository.UsuarioRepository;
import UTP.Zum.Dto.RegistroRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioImpl implements UsuarioService{
     @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario registrarUsuario(RegistroRequest registroRequest) throws Exception {
        if (usuarioRepository.existsByCorreoUsuario(registroRequest.getCorreo())) {
            throw new Exception("El correo ya está registrado");
        }

        Usuario u = new Usuario();
        u.setNomUsuario(registroRequest.getNombre());
        u.setCorreoUsuario(registroRequest.getCorreo());
        u.setContraseniaUsuario(passwordEncoder.encode(registroRequest.getContrasenia()));

        // buscar rol ROLE_USER
        Optional<Rol> rolOpt = rolRepository.findByNombreRol("ROLE_USER");
        if (rolOpt.isEmpty()) {
            throw new Exception("Rol ROLE_USER no configurado en la base de datos");
        }
        u.setRol(rolOpt.get());

        return usuarioRepository.save(u);
    }
}
