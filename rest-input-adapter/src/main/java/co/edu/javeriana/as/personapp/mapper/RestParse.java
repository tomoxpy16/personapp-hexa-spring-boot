package co.edu.javeriana.as.personapp.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Utilidades de conversion/validacion de los datos que llegan por REST. */
public final class RestParse {

	private RestParse() {
	}

	public static String required(String value, String field) {
		if (value == null || value.isBlank())
			throw new IllegalArgumentException(field + " is required");
		return value.trim();
	}

	public static Integer toInteger(String value, String field) {
		try {
			return Integer.valueOf(required(value, field));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(field + " must be an integer");
		}
	}

	public static Integer toIntegerOrNull(String value, String field) {
		return value == null || value.isBlank() ? null : toInteger(value, field);
	}

	public static LocalDate toDateOrNull(String value, String field) {
		try {
			return value == null || value.isBlank() ? null : LocalDate.parse(value.trim());
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(field + " must have format yyyy-MM-dd");
		}
	}
}
