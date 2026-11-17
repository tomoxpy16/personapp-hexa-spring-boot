package co.edu.javeriana.as.personapp.application.rules;

import java.time.LocalDate;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidDataException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;

/**
 * Reglas de negocio del modelo persona / profesion / telefono / estudios.
 *
 * Viven en la capa de aplicacion para que se cumplan igual sin importar el
 * adaptador de entrada (REST o CLI) ni el de salida (MariaDB o MongoDB). Los
 * limites de longitud y rango salen del modelo de datos (DDL).
 */
public final class BusinessRules {

	public static final int EDAD_MAXIMA = 150;
	public static final int MAX_ID_PROFESION = 999999; // INT(6)
	public static final String PATRON_TELEFONO = "\\d{10}";

	private BusinessRules() {
	}

	public static void validate(Person person) {
		positivo(person.getIdentification(), "La cedula");
		texto(person.getFirstName(), "El nombre", 45);
		texto(person.getLastName(), "El apellido", 45);
		if (person.getGender() != Gender.MALE && person.getGender() != Gender.FEMALE)
			throw new InvalidDataException("El genero debe ser M o F");
		if (person.getAge() != null && (!person.isValidAge() || person.getAge() > EDAD_MAXIMA))
			throw new InvalidDataException("La edad debe estar entre 0 y " + EDAD_MAXIMA);
	}

	public static void validate(Phone phone) {
		if (phone.getNumber() == null || !phone.getNumber().matches(PATRON_TELEFONO))
			throw new InvalidDataException("El telefono debe tener exactamente 10 digitos numericos");
		texto(phone.getCompany(), "El operador", 45);
	}

	public static void validate(Profession profession) {
		positivo(profession.getIdentification(), "El id de la profesion");
		if (profession.getIdentification() > MAX_ID_PROFESION)
			throw new InvalidDataException("El id de la profesion no puede ser mayor a " + MAX_ID_PROFESION);
		texto(profession.getName(), "El nombre de la profesion", 90);
	}

	public static void validate(Study study) {
		if (study.getGraduationDate() != null && study.getGraduationDate().isAfter(LocalDate.now()))
			throw new InvalidDataException("La fecha de grado no puede ser futura");
		if (study.getUniversityName() != null && study.getUniversityName().length() > 50)
			throw new InvalidDataException("La universidad no puede tener mas de 50 caracteres");
	}

	private static void positivo(Integer valor, String campo) {
		if (valor == null || valor <= 0)
			throw new InvalidDataException(campo + " debe ser un numero mayor que 0");
	}

	private static void texto(String valor, String campo, int max) {
		if (valor == null || valor.isBlank())
			throw new InvalidDataException(campo + " es obligatorio");
		if (valor.length() > max)
			throw new InvalidDataException(campo + " no puede tener mas de " + max + " caracteres");
	}
}
