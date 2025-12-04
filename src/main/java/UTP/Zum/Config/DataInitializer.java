package UTP.Zum.Config;

import UTP.Zum.Model.Rol;
import UTP.Zum.Persistencia.Repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        if (rolRepository.findByNombreRol("DOCENTE").isEmpty()) {
            Rol rolDocente = new Rol();
            rolDocente.setNombreRol("DOCENTE");
            rolRepository.save(rolDocente);
            System.out.println("DOCENTE creado");
        }
        if (rolRepository.findByNombreRol("ESTUDIANTE").isEmpty()) {
            Rol rolEstudiante = new Rol();
            rolEstudiante.setNombreRol("ESTUDIANTE");
            rolRepository.save(rolEstudiante);
            System.out.println("ESTUDIANTE creado");
        }

        System.out.println("Inicialización completa");
    }
}

