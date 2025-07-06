package ar.edu.utn.frba.inventariobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main class for bootstrapping the Inventario Backend Spring Boot application.
 * <p>
 * This class contains the {@code main} method that serves as the entry point to
 * the application. It uses {@link SpringApplication#run(Class, String...)} to
 * launch the application context.
 * </p>
 */
@SpringBootApplication
public class InventarioBackendApplication {

	/**
	 * The main method that starts the Spring Boot application.
	 *
	 * @param args
	 *            application arguments passed from the command line.
	 */
	public static void main(String[] args) {
		SpringApplication.run(InventarioBackendApplication.class, args);
	}

}
