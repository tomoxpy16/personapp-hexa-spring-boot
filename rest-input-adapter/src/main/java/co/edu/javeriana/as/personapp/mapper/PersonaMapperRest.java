package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

@Mapper
public class PersonaMapperRest {

	public PersonaResponse fromDomainToAdapterRestMaria(Person person) {
		return fromDomainToAdapterRest(person, "MariaDB");
	}

	public PersonaResponse fromDomainToAdapterRestMongo(Person person) {
		return fromDomainToAdapterRest(person, "MongoDB");
	}

	public PersonaResponse fromDomainToAdapterRest(Person person, String database) {
		return new PersonaResponse(
				person.getIdentification() + "",
				person.getFirstName(),
				person.getLastName(),
				person.getAge() != null ? person.getAge() + "" : null,
				person.getGender().toString(),
				database,
				"OK");
	}

	/**
	 * CORRECCION: en la plantilla retornaba new Person() vacio, por lo que el
	 * POST nunca guardaba los datos enviados.
	 */
	public Person fromAdapterToDomain(PersonaRequest request) {
		Person person = new Person();
		person.setIdentification(RestParse.toInteger(request.getDni(), "dni"));
		person.setFirstName(RestParse.required(request.getFirstName(), "firstName"));
		person.setLastName(RestParse.required(request.getLastName(), "lastName"));
		person.setGender(toGender(request.getSex()));
		person.setAge(RestParse.toIntegerOrNull(request.getAge(), "age"));
		if (!person.isValidAge())
			throw new IllegalArgumentException("age must be >= 0");
		return person;
	}

	private Gender toGender(String sex) {
		String s = RestParse.required(sex, "sex").trim().toUpperCase();
		switch (s) {
		case "M":
		case "MALE":
			return Gender.MALE;
		case "F":
		case "FEMALE":
			return Gender.FEMALE;
		default:
			return Gender.OTHER;
		}
	}

}
