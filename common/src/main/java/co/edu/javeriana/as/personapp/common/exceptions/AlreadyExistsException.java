package co.edu.javeriana.as.personapp.common.exceptions;

public class AlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public AlreadyExistsException(String mensaje) {
		super(mensaje);
	}

}
