package UTP.Zum.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import UTP.Zum.Model.Reunion;
import UTP.Zum.Model.Usuario;
import UTP.Zum.Persistencia.Repository.UsuarioRepository;
import UTP.Zum.Services.ReunionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Controller
public class ReunionController {

    @Autowired
    private ReunionService reunionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario obtenerUsuarioActual(Authentication auth) {
        return usuarioRepository.findByCorreoUsuario(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private boolean esDocente(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("DOCENTE") || a.getAuthority().equals("ROLE_DOCENTE"));
    }

    @GetMapping("/programar")
    public String mostrarProgramar(Authentication auth, Model model) {
        Usuario usuario = obtenerUsuarioActual(auth);
        boolean puedeCrear = esDocente(auth);
        model.addAttribute("usuario", usuario);
        model.addAttribute("puedeCrear", puedeCrear);
        model.addAttribute("reuniones", reunionService.obtenerReunionesProximas());
        
        return "Reunion/programar";
    }

    //crear reunión
    @PostMapping("/programar")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ROLE_DOCENTE')")
    public String crearReunion(
            @RequestParam("titulo") String titulo,
            @RequestParam("fecha") String fecha,
            @RequestParam("hora") String hora,
            @RequestParam("duracion") String duracion,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        
        try {
            Usuario creador = obtenerUsuarioActual(auth);
            
            Reunion reunion = new Reunion();
            reunion.setTitulo(titulo);
            
            // Combinar fecha y hora en LocalDateTime
            LocalDate fechaLocal = LocalDate.parse(fecha);
            LocalTime horaLocal = LocalTime.parse(hora);
            reunion.setFechaDeInicio(LocalDateTime.of(fechaLocal, horaLocal));
            
            // Parsear duración (formato HH:mm)
            String[] partes = duracion.split(":");
            int horas = Integer.parseInt(partes[0]);
            int minutos = Integer.parseInt(partes[1]);
            reunion.setDuracionMin(horas * 60 + minutos);
            
            reunionService.crearReunion(reunion, creador);
            redirectAttributes.addFlashAttribute("mensaje", "Reunión creada exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la reunión: " + e.getMessage());
        }
        
        return "redirect:/programar";
    }

    //para unirse a la sala
    @GetMapping("/sala/{codigo}")
    public String mostrarSala(@PathVariable String codigo, Authentication auth, Model model) {
        Optional<Reunion> reunionOpt = reunionService.buscarPorCodigo(codigo);
        
        if (reunionOpt.isEmpty()) {
            return "redirect:/?error=Reunión no encontrada";
        }
        
        Reunion reunion = reunionOpt.get();
        Usuario usuario = obtenerUsuarioActual(auth);
        boolean esCreador = reunion.getCreador().getUsuarioId().equals(usuario.getUsuarioId());
        boolean puedeGrabar = esDocente(auth) && esCreador;
        
        model.addAttribute("reunion", reunion);
        model.addAttribute("usuario", usuario);
        model.addAttribute("esCreador", esCreador);
        model.addAttribute("puedeGrabar", puedeGrabar);
        
        if (esCreador && !reunion.getActiva()) {
            reunionService.iniciarReunion(reunion.getReunionId());
        }
        
        return "Reunion/sala";
    }

    @PostMapping("/unirse")
    public String unirseReunion(@RequestParam("codigo") String codigo, RedirectAttributes redirectAttributes) {
        Optional<Reunion> reunionOpt = reunionService.buscarPorCodigo(codigo.toUpperCase().trim());
        
        if (reunionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Código de reunión inválido");
            return "redirect:/";
        }
        
        return "redirect:/sala/" + codigo.toUpperCase().trim();
    }

    @GetMapping("/grabaciones")
    public String mostrarGrabaciones(Model model) {
        model.addAttribute("grabaciones", reunionService.obtenerGrabaciones());
        return "Reunion/grabaciones";
    }

    @GetMapping("/historial")
    public String mostrarHistorial(Authentication auth, Model model) {
        Usuario usuario = obtenerUsuarioActual(auth);
        boolean esProfesor = esDocente(auth);
        if (esProfesor) {
            model.addAttribute("reuniones", reunionService.obtenerReunionesDelUsuario(usuario));
        } else {
            model.addAttribute("reuniones", reunionService.obtenerTodasLasReuniones());
        }
        model.addAttribute("esDocente", esProfesor);
        return "Reunion/historial";
    }

    @PostMapping("/sala/{codigo}/finalizar")
    public String finalizarReunion(@PathVariable String codigo, Authentication auth) {
        Optional<Reunion> reunionOpt = reunionService.buscarPorCodigo(codigo);
        
        if (reunionOpt.isPresent()) {
            Reunion reunion = reunionOpt.get();
            Usuario usuario = obtenerUsuarioActual(auth);
            
            if (reunion.getCreador().getUsuarioId().equals(usuario.getUsuarioId())) {
                reunionService.finalizarReunion(reunion.getReunionId());
            }
        }
        
        return "redirect:/";
    }

    @PostMapping("/sala/{codigo}/grabar")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ROLE_DOCENTE')")
    public String habilitarGrabacion(@PathVariable String codigo, Authentication auth) {
        Optional<Reunion> reunionOpt = reunionService.buscarPorCodigo(codigo);
        
        if (reunionOpt.isPresent()) {
            Reunion reunion = reunionOpt.get();
            Usuario usuario = obtenerUsuarioActual(auth);
            
            if (reunion.getCreador().getUsuarioId().equals(usuario.getUsuarioId())) {
                reunionService.habilitarGrabacion(reunion.getReunionId());
            }
        }
        
        return "redirect:/sala/" + codigo;
    }
}

