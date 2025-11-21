package UTP.Zum;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import UTP.Zum.Model.Rol;
import UTP.Zum.Persistencia.Repository.RolRepository;

@SpringBootApplication
public class ZumApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZumApplication.class, args);
	}
	
}
