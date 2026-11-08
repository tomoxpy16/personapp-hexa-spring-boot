package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.mapper.PersonaMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("personOutputAdapterMaria")
@Transactional
public class PersonOutputAdapterMaria implements PersonOutputPort {

	@Autowired
	private PersonaRepositoryMaria personaRepositoryMaria;

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

	@Override
	public Person save(Person person) {
		log.debug("Into save on Adapter MariaDB");
		PersonaEntity persistedPersona = personaRepositoryMaria.save(personaMapperMaria.fromDomainToAdapter(person));
		return personaMapperMaria.fromAdapterToDomainShallow(persistedPersona);
	}

	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete on Adapter MariaDB");
		personaRepositoryMaria.deleteById(identification);
		return personaRepositoryMaria.findById(identification).isEmpty();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Person> find() {
		log.debug("Into find on Adapter MariaDB");
		return personaRepositoryMaria.findAll().stream().map(personaMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Person findById(Integer identification) {
		log.debug("Into findById on Adapter MariaDB");
		return personaRepositoryMaria.findById(identification).map(personaMapperMaria::fromAdapterToDomain)
				.orElse(null);
	}

}
