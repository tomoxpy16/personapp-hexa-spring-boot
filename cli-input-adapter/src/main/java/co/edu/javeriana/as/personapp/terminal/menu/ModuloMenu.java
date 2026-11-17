package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.List;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import lombok.extern.slf4j.Slf4j;

/**
 * Plantilla comun de los menus de cada modulo: primero se elige el motor de
 * persistencia (MariaDB / MongoDB) y luego la operacion CRUD.
 */
@Slf4j
public abstract class ModuloMenu {

	private static final int OPCION_REGRESAR = 0;
	private static final int PERSISTENCIA_MARIADB = 1;
	private static final int PERSISTENCIA_MONGODB = 2;

	/** Nombre en plural del modulo, p.ej. "personas". */
	protected abstract String nombreModulo();

	/** Inyecta el puerto de salida (MARIA / MONGO) en el adaptador. */
	protected abstract void seleccionarMotor(String dbOption) throws InvalidOptionException;

	/** Etiquetas de las opciones; la opcion N corresponde a la posicion N-1. */
	protected abstract List<String> opciones();

	/** Ejecuta la opcion elegida (1..N). */
	protected abstract void ejecutar(int opcion, CliInput input) throws Exception;

	public void iniciarMenu(CliInput input) {
		boolean salir = false;
		do {
			System.out.println("---------------------- Modulo de " + nombreModulo());
			System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
			System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
			System.out.println(OPCION_REGRESAR + " para regresar");
			int opcion = input.readInt("Ingrese una opcion: ");
			try {
				switch (opcion) {
				case OPCION_REGRESAR:
					salir = true;
					break;
				case PERSISTENCIA_MARIADB:
					seleccionarMotor("MARIA");
					menuOpciones(input, "MariaDB");
					break;
				case PERSISTENCIA_MONGODB:
					seleccionarMotor("MONGO");
					menuOpciones(input, "MongoDB");
					break;
				default:
					System.out.println("La opcion elegida no es valida.");
				}
			} catch (InvalidOptionException e) {
				log.warn(e.getMessage());
			}
		} while (!salir);
	}

	private void menuOpciones(CliInput input, String motor) {
		List<String> opciones = opciones();
		boolean salir = false;
		do {
			System.out.println("---------------------- " + nombreModulo() + " en " + motor);
			for (int i = 0; i < opciones.size(); i++)
				System.out.println((i + 1) + " para " + opciones.get(i));
			System.out.println(OPCION_REGRESAR + " para regresar");
			int opcion = input.readInt("Ingrese una opcion: ");
			if (opcion == OPCION_REGRESAR) {
				salir = true;
			} else if (opcion < 1 || opcion > opciones.size()) {
				System.out.println("La opcion elegida no es valida.");
			} else {
				try {
					ejecutar(opcion, input);
				} catch (Exception e) {
					System.out.println("ERROR: " + e.getMessage());
				}
			}
		} while (!salir);
	}
}
