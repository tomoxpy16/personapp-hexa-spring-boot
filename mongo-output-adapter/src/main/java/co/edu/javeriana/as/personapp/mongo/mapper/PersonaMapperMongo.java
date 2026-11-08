package co.edu.javeriana.as.personapp.mongo.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;

/**
 * Mapper PersonaDocument (adapter MongoDB) <-> Person (dominio).
 *
 * CORRECCION: igual que en MariaDB, se cortan los ciclos Persona -> Estudio ->
 * Persona usando referencias "shallow" (sin colecciones).
 */
@Mapper
public class PersonaMapperMongo {

	@Autowired
	private EstudiosMapperMongo estudiosMapperMongo;

	@Autowired
	private TelefonoMapperMongo telefonoMapperMongo;

	/**
	 * Domain -> Adapter. telefonos/estudios son @ReadOnlyProperty (se resuelven
	 * por lookup), por eso no se mapean al guardar.
	 */
	public PersonaDocument fromDomainToAdapter(Person person) {
		PersonaDocument personaDocument = new PersonaDocument();
		personaDocument.setId(person.getIdentification());
		personaDocument.setNombre(person.getFirstName());
		personaDocument.setApellido(person.getLastName());
		personaDocument.setGenero(validateGenero(person.getGender()));
		personaDocument.setEdad(validateEdad(person.getAge()));
		return personaDocument;
	}

	private String validateGenero(Gender gender) {
		return gender == Gender.FEMALE ? "F" : gender == Gender.MALE ? "M" : " ";
	}

	private Integer validateEdad(Integer age) {
		return age != null && age >= 0 ? age : null;
	}

	/** Adapter -> Domain completo (con telefonos y estudios). */
	public Person fromAdapterToDomain(PersonaDocument personaDocument) {
		Person person = fromAdapterToDomainShallow(personaDocument);
		person.setStudies(validateStudies(personaDocument.getEstudios()));
		person.setPhoneNumbers(validatePhones(personaDocument.getTelefonos()));
		return person;
	}

	/** Adapter -> Domain sin relaciones (corta la recursion). */
	public Person fromAdapterToDomainShallow(PersonaDocument personaDocument) {
		Person person = new Person();
		person.setIdentification(personaDocument.getId());
		person.setFirstName(personaDocument.getNombre());
		person.setLastName(personaDocument.getApellido());
		person.setGender(validateGender(personaDocument.getGenero()));
		person.setAge(validateAge(personaDocument.getEdad()));
		return person;
	}

	private Gender validateGender(String genero) {
		return "F".equals(genero) ? Gender.FEMALE : "M".equals(genero) ? Gender.MALE : Gender.OTHER;
	}

	private Integer validateAge(Integer edad) {
		return edad != null && edad >= 0 ? edad : null;
	}

	private List<Study> validateStudies(List<EstudiosDocument> estudiosDocuments) {
		return estudiosDocuments != null && !estudiosDocuments.isEmpty()
				? estudiosDocuments.stream().map(estudiosMapperMongo::fromAdapterToDomain).collect(Collectors.toList())
				: new ArrayList<Study>();
	}

	private List<Phone> validatePhones(List<TelefonoDocument> telefonosDocuments) {
		return telefonosDocuments != null && !telefonosDocuments.isEmpty()
				? telefonosDocuments.stream().map(telefonoMapperMongo::fromAdapterToDomain).collect(Collectors.toList())
				: new ArrayList<Phone>();
	}
}
