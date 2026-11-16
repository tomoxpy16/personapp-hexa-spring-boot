package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada REST (cumple el rol de "Service"): elige el puerto de
 * salida segun la BD, invoca el caso de uso y mapea dominio <-> modelo REST.
 */
@Slf4j
@Adapter
public class PersonaInputAdapterRest {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperRest personaMapperRest;

	@Autowired
	private TelefonoMapperRest telefonoMapperRest;

	/** Se crea un caso de uso por peticion: evita estado compartido entre hilos. */
	private PersonInputPort useCase(DatabaseOption option) {
		return new PersonUseCase(option == DatabaseOption.MARIA ? personOutputPortMaria : personOutputPortMongo);
	}

	public List<PersonaResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial PersonaEntity in Input Adapter");
		DatabaseOption option = DatabaseSelector.parse(database);
		String label = DatabaseSelector.label(option);
		return useCase(option).findAll().stream().map(p -> personaMapperRest.fromDomainToAdapterRest(p, label))
				.collect(Collectors.toList());
	}

	public PersonaResponse buscarPersona(String database, Integer cc) throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(database);
		return personaMapperRest.fromDomainToAdapterRest(useCase(option).findOne(cc), DatabaseSelector.label(option));
	}

	public Integer contarPersonas(String database) throws InvalidOptionException {
		return useCase(DatabaseSelector.parse(database)).count();
	}

	public PersonaResponse crearPersona(PersonaRequest request) throws InvalidOptionException, AlreadyExistsException {
		DatabaseOption option = DatabaseSelector.parse(request.getDatabase());
		return personaMapperRest.fromDomainToAdapterRest(
				useCase(option).create(personaMapperRest.fromAdapterToDomain(request)), DatabaseSelector.label(option));
	}

	public PersonaResponse editarPersona(Integer cc, PersonaRequest request)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(request.getDatabase());
		request.setDni(cc + "");
		return personaMapperRest.fromDomainToAdapterRest(
				useCase(option).edit(cc, personaMapperRest.fromAdapterToDomain(request)),
				DatabaseSelector.label(option));
	}

	public Boolean eliminarPersona(String database, Integer cc) throws InvalidOptionException, NoExistException {
		return useCase(DatabaseSelector.parse(database)).drop(cc);
	}

	public List<TelefonoResponse> telefonosPersona(String database, Integer cc)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(database);
		String label = DatabaseSelector.label(option);
		return useCase(option).getPhones(cc).stream().map(t -> telefonoMapperRest.fromDomainToAdapterRest(t, label))
				.collect(Collectors.toList());
	}

}
