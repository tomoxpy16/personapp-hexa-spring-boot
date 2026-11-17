package co.edu.javeriana.as.personapp.terminal.adapter;

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
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

@Adapter
public class EstudioInputAdapterCli {

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
	private EstudioMapperCli estudioMapperCli;

	private StudyInputPort studyInputPort;

	public void setStudyOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			studyInputPort = new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			studyInputPort = new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public void historial() {
		studyInputPort.findAll().stream().map(estudioMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
	}

	public void buscar(Integer idProfesion, Integer ccPersona) throws NoExistException {
		System.out.println(estudioMapperCli.fromDomainToAdapterCli(studyInputPort.findOne(idProfesion, ccPersona)));
	}

	public void crear(EstudioModelCli model) throws AlreadyExistsException, NoExistException {
		System.out.println("Creado: " + estudioMapperCli.fromDomainToAdapterCli(
				studyInputPort.create(estudioMapperCli.fromAdapterCliToDomain(model))));
	}

	public void editar(EstudioModelCli model) throws NoExistException {
		System.out.println("Editado: " + estudioMapperCli.fromDomainToAdapterCli(studyInputPort
				.edit(model.getIdProfesion(), model.getCcPersona(), estudioMapperCli.fromAdapterCliToDomain(model))));
	}

	public void eliminar(Integer idProfesion, Integer ccPersona) throws NoExistException {
		System.out.println(
				studyInputPort.drop(idProfesion, ccPersona) ? "Estudio eliminado" : "No se pudo eliminar el estudio");
	}

	public void contar() {
		System.out.println("Total de estudios: " + studyInputPort.count());
	}
}
