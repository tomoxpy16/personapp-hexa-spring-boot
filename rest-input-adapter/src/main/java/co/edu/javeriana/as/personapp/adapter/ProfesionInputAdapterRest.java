package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.mapper.ProfesionMapperRest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class ProfesionInputAdapterRest {

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private ProfesionMapperRest profesionMapperRest;

	@Autowired
	private EstudioMapperRest estudioMapperRest;

	private ProfessionInputPort useCase(DatabaseOption option) {
		return new ProfessionUseCase(
				option == DatabaseOption.MARIA ? professionOutputPortMaria : professionOutputPortMongo);
	}

	public List<ProfesionResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial Profesion in Input Adapter");
		DatabaseOption option = DatabaseSelector.parse(database);
		String label = DatabaseSelector.label(option);
		return useCase(option).findAll().stream().map(p -> profesionMapperRest.fromDomainToAdapterRest(p, label))
				.collect(Collectors.toList());
	}

	public ProfesionResponse buscar(String database, Integer id) throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(database);
		return profesionMapperRest.fromDomainToAdapterRest(useCase(option).findOne(id), DatabaseSelector.label(option));
	}

	public Integer contar(String database) throws InvalidOptionException {
		return useCase(DatabaseSelector.parse(database)).count();
	}

	public ProfesionResponse crear(ProfesionRequest request) throws InvalidOptionException, AlreadyExistsException {
		DatabaseOption option = DatabaseSelector.parse(request.getDatabase());
		return profesionMapperRest.fromDomainToAdapterRest(
				useCase(option).create(profesionMapperRest.fromAdapterToDomain(request)),
				DatabaseSelector.label(option));
	}

	public ProfesionResponse editar(Integer id, ProfesionRequest request)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(request.getDatabase());
		request.setId(id + "");
		return profesionMapperRest.fromDomainToAdapterRest(
				useCase(option).edit(id, profesionMapperRest.fromAdapterToDomain(request)),
				DatabaseSelector.label(option));
	}

	public Boolean eliminar(String database, Integer id) throws InvalidOptionException, NoExistException {
		return useCase(DatabaseSelector.parse(database)).drop(id);
	}

	public List<EstudioResponse> estudios(String database, Integer id) throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseSelector.parse(database);
		String label = DatabaseSelector.label(option);
		return useCase(option).getStudies(id).stream().map(e -> estudioMapperRest.fromDomainToAdapterRest(e, label))
				.collect(Collectors.toList());
	}
}
