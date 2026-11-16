package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;

@Controller
public class MenuPrincipal {

	// Beans
	@Autowired
	private PersonaInputAdapterCli personaInputAdapterCli;
	@Autowired
	private TelefonoInputAdapterCli telefonoInputAdapterCli;

	private static final int SALIR = 0;
	private static final int MODULO_PERSONA = 1;
	private static final int MODULO_PROFESION = 2;
	private static final int MODULO_TELEFONO = 3;
	private static final int MODULO_ESTUDIO = 4;

	public void inicio() {
		Scanner keyboard = new Scanner(System.in);
		CliInput input = new CliInput(keyboard);
		PersonaMenu personaMenu = new PersonaMenu(personaInputAdapterCli);
		TelefonoMenu telefonoMenu = new TelefonoMenu(telefonoInputAdapterCli);

		boolean salir = false;
		do {
			mostrarMenu();
			int opcion = input.readInt("Ingrese una opcion: ");
			switch (opcion) {
			case SALIR -> salir = true;
			case MODULO_PERSONA -> personaMenu.iniciarMenu(input);
			case MODULO_PROFESION, MODULO_ESTUDIO -> System.out.println("Modulo en construccion");
			case MODULO_TELEFONO -> telefonoMenu.iniciarMenu(input);
			default -> System.out.println("La opcion elegida no es valida.");
			}
		} while (!salir);
		keyboard.close();
	}

	private void mostrarMenu() {
		System.out.println("======================");
		System.out.println(MODULO_PERSONA + " para trabajar con el Modulo de Personas");
		System.out.println(MODULO_PROFESION + " para trabajar con el Modulo de Profesiones");
		System.out.println(MODULO_TELEFONO + " para trabajar con el Modulo de Telefonos");
		System.out.println(MODULO_ESTUDIO + " para trabajar con el Modulo de Estudios");
		System.out.println(SALIR + " para Salir");
	}

}
