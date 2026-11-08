package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;

/**
 * Mapper PersonaEntity (adapter MariaDB) <-> Person (dominio).
 *
 * CORRECCION: en la plantilla se mapeaba Persona -> Estudio -> Persona -> ...
 * de forma recursiva (StackOverflowError). Ahora las relaciones hijas
 * (telefonos, estudios) se mapean con referencias "shallow" (solo atributos
 * propios, sin colecciones), lo que corta el ciclo.
 */
@Mapper
public class PersonaMapperMaria {

	@Autowired
	private EstudiosMapperMaria estudiosMapperMaria;

	@Autowired
	private TelefonoMapperMaria telefonoMapperMaria;

	/**
	 * Domain -> Adapter. Solo atributos propios: los telefonos y estudios se
	 * persisten desde sus propios adaptadores.
	 */
	public PersonaEntity fromDomainToAdapter(Person person) {
		PersonaEntity personaEntity = fromDomainToAdapterShallow(person);
		personaEntity.setEstudios(new ArrayList<EstudiosEntity>());
		personaEntity.setTelefonos(new ArrayList<TelefonoEntity>());
		return personaEntity;
	}

	/** Domain -> Adapter sin relaciones (para usar como referencia). */
	public PersonaEntity fromDomainToAdapterShallow(Person person) {
		PersonaEntity personaEntity = new PersonaEntity();
		personaEntity.setCc(person.getIdentification());
		personaEntity.setNombre(person.getFirstName());
		personaEntity.setApellido(person.getLastName());
		personaEntity.setGenero(validateGenero(person.getGender()));
		personaEntity.setEdad(validateEdad(person.getAge()));
		return personaEntity;
	}

	private Character validateGenero(Gender gender) {
		return gender == Gender.FEMALE ? 'F' : gender == Gender.MALE ? 'M' : ' ';
	}

	private Integer validateEdad(Integer age) {
		return age != null && age >= 0 ? age : null;
	}

	/** Adapter -> Domain completo (con telefonos y estudios). */
	public Person fromAdapterToDomain(PersonaEntity personaEntity) {
		Person person = fromAdapterToDomainShallow(personaEntity);
		person.setStudies(validateStudies(personaEntity.getEstudios()));
		person.setPhoneNumbers(validatePhones(personaEntity.getTelefonos()));
		return person;
	}

	/** Adapter -> Domain sin relaciones (corta la recursion). */
	public Person fromAdapterToDomainShallow(PersonaEntity personaEntity) {
		Person person = new Person();
		person.setIdentification(personaEntity.getCc());
		person.setFirstName(personaEntity.getNombre());
		person.setLastName(personaEntity.getApellido());
		person.setGender(validateGender(personaEntity.getGenero()));
		person.setAge(validateAge(personaEntity.getEdad()));
		return person;
	}

	private Gender validateGender(Character genero) {
		if (genero == null)
			return Gender.OTHER;
		return genero == 'F' ? Gender.FEMALE : genero == 'M' ? Gender.MALE : Gender.OTHER;
	}

	private Integer validateAge(Integer edad) {
		return edad != null && edad >= 0 ? edad : null;
	}

	private List<Study> validateStudies(List<EstudiosEntity> estudiosEntity) {
		return estudiosEntity != null && !estudiosEntity.isEmpty()
				? estudiosEntity.stream().map(estudiosMapperMaria::fromAdapterToDomain).collect(Collectors.toList())
				: new ArrayList<Study>();
	}

	private List<Phone> validatePhones(List<TelefonoEntity> telefonoEntities) {
		return telefonoEntities != null && !telefonoEntities.isEmpty()
				? telefonoEntities.stream().map(telefonoMapperMaria::fromAdapterToDomain).collect(Collectors.toList())
				: new ArrayList<Phone>();
	}
}
