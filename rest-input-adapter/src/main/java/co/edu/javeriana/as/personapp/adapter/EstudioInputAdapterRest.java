package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class EstudioInputAdapterRest {

	@Autowired
	@Qualifier("studyOutputAdapterMaria")
	private StudyOutputPort studyOutputPortMaria;

	@Autowired
	@Qualifier("studyOutputAdapterMongo")
	private StudyOutputPort studyOutputPortMongo;

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private EstudioMapperRest estudioMapperRest;

	private StudyInputPort useCase(DatabaseOption option) {
		return option == DatabaseOption.MARIA
				? new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria)
				: new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
	}

	public List<EstudioResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial Estudio in Input Adapter");
		DatabaseOption option = DatabaseSelector.parse(database);
		String label = DatabaseSelector.label(option);
		return useCase(option).findAll().stream().map(e -> estudioMapperRest.fromDomainToAdapterRest(e, label))
				.collect(Collectors.toList());
	}

	public EstudioResponse buscar(String database, Integer professionId, Integer personId)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(database);
		return estudioMapperRest.fromDomainToAdapterRest(useCase(option).findOne(professionId, personId),
				DatabaseSelector.label(option));
	}

	public Integer contar(String database) throws InvalidOptionException {
		return useCase(DatabaseSelector.parse(database)).count();
	}

	public EstudioResponse crear(EstudioRequest request)
			throws InvalidOptionException, AlreadyExistsException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(request.getDatabase());
		return estudioMapperRest.fromDomainToAdapterRest(
				useCase(option).create(estudioMapperRest.fromAdapterToDomain(request)),
				DatabaseSelector.label(option));
	}

	public EstudioResponse editar(Integer professionId, Integer personId, EstudioRequest request)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(request.getDatabase());
		request.setProfessionId(professionId + "");
		request.setPersonId(personId + "");
		return estudioMapperRest.fromDomainToAdapterRest(
				useCase(option).edit(professionId, personId, estudioMapperRest.fromAdapterToDomain(request)),
				DatabaseSelector.label(option));
	}

	public Boolean eliminar(String database, Integer professionId, Integer personId)
			throws InvalidOptionException, NoExistException {
		return useCase(DatabaseSelector.parse(database)).drop(professionId, personId);
	}
}
