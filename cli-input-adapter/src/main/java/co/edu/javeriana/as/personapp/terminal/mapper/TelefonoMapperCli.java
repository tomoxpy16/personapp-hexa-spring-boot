package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

@Mapper
public class TelefonoMapperCli {

	public TelefonoModelCli fromDomainToAdapterCli(Phone phone) {
		Person owner = phone.getOwner();
		return new TelefonoModelCli(phone.getNumber(), phone.getCompany(), owner.getIdentification(),
				owner.getFirstName() != null ? owner.getFirstName() + " " + owner.getLastName() : null);
	}

	/** El duenio solo lleva la cedula; el caso de uso valida que exista. */
	public Phone fromAdapterCliToDomain(TelefonoModelCli model) {
		Person owner = new Person();
		owner.setIdentification(model.getDuenioCc());
		Phone phone = new Phone();
		phone.setNumber(model.getNumero());
		phone.setCompany(model.getOperador());
		phone.setOwner(owner);
		return phone;
	}
}
