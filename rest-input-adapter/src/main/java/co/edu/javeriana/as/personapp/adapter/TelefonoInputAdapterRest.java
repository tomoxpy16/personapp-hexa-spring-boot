package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

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
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class TelefonoInputAdapterRest {

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
	private TelefonoMapperRest telefonoMapperRest;

	private PhoneInputPort useCase(DatabaseOption option) {
		return option == DatabaseOption.MARIA ? new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria)
				: new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
	}

	public List<TelefonoResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial Telefono in Input Adapter");
		DatabaseOption option = DatabaseSelector.parse(database);
		String label = DatabaseSelector.label(option);
		return useCase(option).findAll().stream().map(t -> telefonoMapperRest.fromDomainToAdapterRest(t, label))
				.collect(Collectors.toList());
	}

	public TelefonoResponse buscar(String database, String number) throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(database);
		return telefonoMapperRest.fromDomainToAdapterRest(useCase(option).findOne(number),
				DatabaseSelector.label(option));
	}

	public Integer contar(String database) throws InvalidOptionException {
		return useCase(DatabaseSelector.parse(database)).count();
	}

	public TelefonoResponse crear(TelefonoRequest request)
			throws InvalidOptionException, AlreadyExistsException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(request.getDatabase());
		return telefonoMapperRest.fromDomainToAdapterRest(
				useCase(option).create(telefonoMapperRest.fromAdapterToDomain(request)),
				DatabaseSelector.label(option));
	}

	public TelefonoResponse editar(String number, TelefonoRequest request)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(request.getDatabase());
		request.setNumber(number);
		return telefonoMapperRest.fromDomainToAdapterRest(
				useCase(option).edit(number, telefonoMapperRest.fromAdapterToDomain(request)),
				DatabaseSelector.label(option));
	}

	public Boolean eliminar(String database, String number) throws InvalidOptionException, NoExistException {
		return useCase(DatabaseSelector.parse(database)).drop(number);
	}
}
