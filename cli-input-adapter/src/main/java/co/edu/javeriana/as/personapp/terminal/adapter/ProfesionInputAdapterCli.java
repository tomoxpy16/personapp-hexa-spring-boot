package co.edu.javeriana.as.personapp.terminal.adapter;

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
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.ProfesionMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

@Adapter
public class ProfesionInputAdapterCli {

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private ProfesionMapperCli profesionMapperCli;

	@Autowired
	private EstudioMapperCli estudioMapperCli;

	private ProfessionInputPort professionInputPort;

	public void setProfessionOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public void historial() {
		professionInputPort.findAll().stream().map(profesionMapperCli::fromDomainToAdapterCli)
				.forEach(System.out::println);
	}

	public void buscar(Integer id) throws NoExistException {
		System.out.println(profesionMapperCli.fromDomainToAdapterCli(professionInputPort.findOne(id)));
	}

	public void crear(ProfesionModelCli model) throws AlreadyExistsException {
		System.out.println("Creada: " + profesionMapperCli.fromDomainToAdapterCli(
				professionInputPort.create(profesionMapperCli.fromAdapterCliToDomain(model))));
	}

	public void editar(ProfesionModelCli model) throws NoExistException {
		System.out.println("Editada: " + profesionMapperCli.fromDomainToAdapterCli(
				professionInputPort.edit(model.getId(), profesionMapperCli.fromAdapterCliToDomain(model))));
	}

	public void eliminar(Integer id) throws NoExistException {
		System.out.println(professionInputPort.drop(id) ? "Profesion eliminada" : "No se pudo eliminar la profesion");
	}

	public void contar() {
		System.out.println("Total de profesiones: " + professionInputPort.count());
	}

	public void estudios(Integer id) throws NoExistException {
		professionInputPort.getStudies(id).stream().map(estudioMapperCli::fromDomainToAdapterCli)
				.forEach(System.out::println);
	}
}
