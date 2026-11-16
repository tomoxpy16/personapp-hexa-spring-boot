package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class PhoneUseCase implements PhoneInputPort {

	private PhoneOutputPort phonePersintence;
	private PersonOutputPort personPersintence;

	public PhoneUseCase(@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phonePersintence,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersintence) {
		setPersintence(phonePersintence, personPersintence);
	}

	@Override
	public void setPersintence(PhoneOutputPort phonePersintence, PersonOutputPort personPersintence) {
		this.phonePersintence = phonePersintence;
		this.personPersintence = personPersintence;
	}

	/** Regla de negocio: el duenio del telefono debe existir. */
	private Person validateOwner(Phone phone) throws NoExistException {
		Integer ownerId = phone.getOwner().getIdentification();
		Person owner = ownerId == null ? null : personPersintence.findById(ownerId);
		if (owner == null)
			throw new NoExistException("The owner (person) with id " + ownerId + " does not exist into db");
		return owner;
	}

	@Override
	public Phone create(Phone phone) throws AlreadyExistsException, NoExistException {
		log.debug("Into create on Application Domain");
		if (phonePersintence.findById(phone.getNumber()) != null)
			throw new AlreadyExistsException(
					"The phone " + phone.getNumber() + " already exists into db, cannot be created");
		phone.setOwner(validateOwner(phone));
		return phonePersintence.save(phone);
	}

	@Override
	public Phone edit(String number, Phone phone) throws NoExistException {
		findOne(number);
		phone.setNumber(number);
		phone.setOwner(validateOwner(phone));
		return phonePersintence.save(phone);
	}

	@Override
	public Boolean drop(String number) throws NoExistException {
		findOne(number);
		return phonePersintence.delete(number);
	}

	@Override
	public List<Phone> findAll() {
		log.info("Output: " + phonePersintence.getClass());
		return phonePersintence.find();
	}

	@Override
	public Phone findOne(String number) throws NoExistException {
		Phone phone = phonePersintence.findById(number);
		if (phone != null)
			return phone;
		throw new NoExistException("The phone " + number + " does not exist into db");
	}

	@Override
	public Integer count() {
		return findAll().size();
	}
}
