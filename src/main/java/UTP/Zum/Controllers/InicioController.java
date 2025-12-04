package UTP.Zum.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import UTP.Zum.Dto.RegistroRequest;
import UTP.Zum.Model.Usuario;
import UTP.Zum.Persistencia.Repository.UsuarioRepository;
import UTP.Zum.Services.ReunionService;
import UTP.Zum.Services.UsuarioService;

@Controller
public class InicioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ReunionService reunionService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping({"/", "/index"})
    public String index(Authentication auth, Model model) {
        if (auth != null) {
            Usuario usuario = usuarioRepository.findByCorreoUsuario(auth.getName()).orElse(null);
            model.addAttribute("usuario", usuario);
            model.addAttribute("reuniones", reunionService.obtenerReunionesProximas());
        }
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(name="error", required=false) String error, Model model) {
        if (error != null) model.addAttribute("error", "Credenciales inválidas");
        return "login";
    }

    @GetMapping("/registro")
    public String registroForm() {
        return "register";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute RegistroRequest registroRequest, Model model) {
        try {
            usuarioService.registrarUsuario(registroRequest);
            model.addAttribute("msg", "Registro exitoso. Inicia sesión.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
