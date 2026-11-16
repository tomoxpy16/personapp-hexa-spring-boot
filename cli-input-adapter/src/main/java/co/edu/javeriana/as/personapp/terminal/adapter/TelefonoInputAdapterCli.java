package co.edu.javeriana.as.personapp.terminal.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

@Adapter
public class TelefonoInputAdapterCli {

	@Autowired
	@Qualifier("phoneOutputAdapterMaria")
	private PhoneOutputPort phoneOutputPortMaria;

	@Autowired
	@Qualifier("phoneOutputAdapterMongo")
	private PhoneOutputPort phoneOutputPortMongo;

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private TelefonoMapperCli telefonoMapperCli;

	private PhoneInputPort phoneInputPort;

	public void setPhoneOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			phoneInputPort = new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			phoneInputPort = new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public void historial() {
		phoneInputPort.findAll().stream().map(telefonoMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
	}

	public void buscar(String numero) throws NoExistException {
		System.out.println(telefonoMapperCli.fromDomainToAdapterCli(phoneInputPort.findOne(numero)));
	}

	public void crear(TelefonoModelCli model) throws AlreadyExistsException, NoExistException {
		System.out.println("Creado: " + telefonoMapperCli.fromDomainToAdapterCli(
				phoneInputPort.create(telefonoMapperCli.fromAdapterCliToDomain(model))));
	}

	public void editar(TelefonoModelCli model) throws NoExistException {
		System.out.println("Editado: " + telefonoMapperCli.fromDomainToAdapterCli(
				phoneInputPort.edit(model.getNumero(), telefonoMapperCli.fromAdapterCliToDomain(model))));
	}

	public void eliminar(String numero) throws NoExistException {
		System.out.println(phoneInputPort.drop(numero) ? "Telefono eliminado" : "No se pudo eliminar el telefono");
	}

	public void contar() {
		System.out.println("Total de telefonos: " + phoneInputPort.count());
	}
}
