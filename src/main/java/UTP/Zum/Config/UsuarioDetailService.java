package UTP.Zum.Config;


import UTP.Zum.Model.Usuario;
import UTP.Zum.Persistencia.Repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class UsuarioDetailService implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoUsuario(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        SimpleGrantedAuthority auth = new SimpleGrantedAuthority(usuario.getRol().getNombreRol());
        return new User(usuario.getCorreoUsuario(), usuario.getContraseniaUsuario(), Collections.singletonList(auth));
    }
}