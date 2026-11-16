package co.edu.javeriana.as.personapp.adapter;

import java.util.ArrayList;
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
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

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

	@Autowired
	private EstudioMapperRest estudioMapperRest;

	PersonInputPort personInputPort;

	private String setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMaria);
			return DatabaseOption.MARIA.toString();
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMongo);
			return  DatabaseOption.MONGO.toString();
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public List<PersonaResponse> historial(String database) {
		log.info("Into historial PersonaEntity in Input Adapter");
		try {
			if(setPersonOutputPortInjection(database).equalsIgnoreCase(DatabaseOption.MARIA.toString())){
				return personInputPort.findAll().stream().map(personaMapperRest::fromDomainToAdapterRestMaria)
						.collect(Collectors.toList());
			}else {
				return personInputPort.findAll().stream().map(personaMapperRest::fromDomainToAdapterRestMongo)
						.collect(Collectors.toList());
			}
			
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new ArrayList<PersonaResponse>();
		}
	}

	public PersonaResponse crearPersona(PersonaRequest request) throws AlreadyExistsException {
		try {
			setPersonOutputPortInjection(request.getDatabase());
			Person person = personInputPort.create(personaMapperRest.fromAdapterToDomain(request));
			return personaMapperRest.fromDomainToAdapterRestMaria(person);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			//return new PersonaResponse("", "", "", "", "", "", "");
		}
		return null;
	}


	/** Caso de uso propio de cada peticion (no comparte estado entre hilos). */
	private PersonInputPort useCase(DatabaseOption option) {
		return new PersonUseCase(option == DatabaseOption.MARIA ? personOutputPortMaria : personOutputPortMongo);
	}

	public PersonaResponse buscarPersona(String database, Integer cc) throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(database);
		return personaMapperRest.fromDomainToAdapterRest(useCase(option).findOne(cc), DatabaseSelector.label(option));
	}

	public Integer contarPersonas(String database) throws InvalidOptionException {
		return useCase(DatabaseSelector.parse(database)).count();
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

	public List<EstudioResponse> estudiosPersona(String database, Integer cc)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(database);
		String label = DatabaseSelector.label(option);
		return useCase(option).getStudies(cc).stream().map(e -> estudioMapperRest.fromDomainToAdapterRest(e, label))
				.collect(Collectors.toList());
	}
}
