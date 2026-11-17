package co.edu.javeriana.as.personapp.terminal.mapper;

import java.time.LocalDate;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

@Mapper
public class EstudioMapperCli {

	public EstudioModelCli fromDomainToAdapterCli(Study study) {
		Person person = study.getPerson();
		Profession profession = study.getProfession();
		return new EstudioModelCli(
				profession.getIdentification(),
				profession.getName(),
				person.getIdentification(),
				person.getFirstName() != null ? person.getFirstName() + " " + person.getLastName() : null,
				study.getGraduationDate() != null ? study.getGraduationDate().toString() : null,
				study.getUniversityName());
	}

	/** Persona y profesion solo llevan el id; el caso de uso valida que existan. */
	public Study fromAdapterCliToDomain(EstudioModelCli model) {
		Person person = new Person();
		person.setIdentification(model.getCcPersona());
		Profession profession = new Profession();
		profession.setIdentification(model.getIdProfesion());
		Study study = new Study();
		study.setPerson(person);
		study.setProfession(profession);
		study.setGraduationDate(model.getFecha() != null ? LocalDate.parse(model.getFecha()) : null);
		study.setUniversityName(model.getUniversidad());
		return study;
	}
}
