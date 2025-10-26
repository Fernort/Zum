package UTP.Zum;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import UTP.Zum.Persistencia.Rol;
import UTP.Zum.Persistencia.Repository.RolRepository;

@SpringBootApplication
public class ZumApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZumApplication.class, args);
	}
	@Bean
    public CommandLineRunner createRolesIfNotExist(RolRepository rolRepository) {
        return args -> {
            if (rolRepository.findByNombreRol("ROLE_USER").isEmpty()) {
                Rol r = new Rol();
                r.setNombreRol("ROLE_USER");
                rolRepository.save(r);
            }
            if (rolRepository.findByNombreRol("ROLE_ADMIN").isEmpty()) {
                Rol r2 = new Rol();
                r2.setNombreRol("ROLE_ADMIN");
                rolRepository.save(r2);
            }
        };
    }
}
