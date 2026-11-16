package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class PersonUseCase implements PersonInputPort {

	private PersonOutputPort personPersintence;

	public PersonUseCase(@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersintence) {
		this.personPersintence = personPersintence;
	}

	@Override
	public void setPersintence(PersonOutputPort personPersintence) {
		this.personPersintence = personPersintence;
	}

	@Override
	public Person create(Person person) throws AlreadyExistsException {
		log.debug("Into create on Application Domain");
		if (personPersintence.findById(person.getIdentification()) != null)
			throw new AlreadyExistsException(
					"The person with id " + person.getIdentification() + " already exists into db, cannot be created");
		return personPersintence.save(person);
	}

	@Override
	public Person edit(Integer identification, Person person) throws NoExistException {
		findOne(identification);
		person.setIdentification(identification);
		return personPersintence.save(person);
	}

	@Override
	public Boolean drop(Integer identification) throws NoExistException {
		findOne(identification);
		return personPersintence.delete(identification);
	}

	@Override
	public List<Person> findAll() {
		log.info("Output: " + personPersintence.getClass());
		return personPersintence.find();
	}

	@Override
	public Person findOne(Integer identification) throws NoExistException {
		Person person = personPersintence.findById(identification);
		if (person != null)
			return person;
		throw new NoExistException("The person with id " + identification + " does not exist into db");
	}

	@Override
	public Integer count() {
		return findAll().size();
	}

	@Override
	public List<Phone> getPhones(Integer identification) throws NoExistException {
		return findOne(identification).getPhoneNumbers();
	}

	@Override
	public List<Study> getStudies(Integer identification) throws NoExistException {
		return findOne(identification).getStudies();
	}
}
