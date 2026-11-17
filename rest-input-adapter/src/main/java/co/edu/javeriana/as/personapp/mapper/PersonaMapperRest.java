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
				person.getIdentification()+"", 
				person.getFirstName(), 
				person.getLastName(), 
				person.getAge()+"", 
				person.getGender().toString(), 
				database,
				"OK");
	}

	public Person fromAdapterToDomain(PersonaRequest request) {
		// CORRECCION DEL MAPPER: antes retornaba new Person() vacio
		Person person = new Person();
		person.setIdentification(RestParse.toInteger(request.getDni(), "dni"));
		person.setFirstName(RestParse.required(request.getFirstName(), "firstName"));
		person.setLastName(RestParse.required(request.getLastName(), "lastName"));
		person.setGender(toGender(RestParse.required(request.getSex(), "sex")));
		person.setAge(RestParse.toIntegerOrNull(request.getAge(), "age"));
		if (person.getAge() != null && !person.isValidAge())
			throw new IllegalArgumentException("age must be >= 0");
		return person;
	}
		

	private Gender toGender(String sex) {
		String s = sex.trim().toUpperCase();
		if (s.equals("M") || s.equals("MALE"))
			return Gender.MALE;
		if (s.equals("F") || s.equals("FEMALE"))
			return Gender.FEMALE;
		return Gender.OTHER;
	}
}
