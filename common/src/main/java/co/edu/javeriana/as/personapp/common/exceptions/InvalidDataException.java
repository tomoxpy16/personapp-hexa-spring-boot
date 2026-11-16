package co.edu.javeriana.as.personapp.common.exceptions;

/**
 * Dato que incumple una regla de negocio (edad fuera de rango, telefono que no
 * tiene 10 digitos, etc.). Es no chequeada para no cambiar las firmas de los
 * puertos; el adaptador REST la responde como 400 y el CLI muestra el mensaje.
 */
public class InvalidDataException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public InvalidDataException(String mensaje) {
		super(mensaje);
	}

}
