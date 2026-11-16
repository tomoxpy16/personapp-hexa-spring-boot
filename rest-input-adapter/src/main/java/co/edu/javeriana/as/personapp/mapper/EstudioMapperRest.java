package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;

@Mapper
public class EstudioMapperRest {

	public EstudioResponse fromDomainToAdapterRest(Study study, String database) {
		return new EstudioResponse(
				study.getProfession().getIdentification() + "",
				study.getPerson().getIdentification() + "",
				study.getGraduationDate() != null ? study.getGraduationDate().toString() : null,
				study.getUniversityName(),
				database,
				"OK");
	}

	/** Persona y profesion solo llevan el id: el caso de uso valida que existan. */
	public Study fromAdapterToDomain(EstudioRequest request) {
		Person person = new Person();
		person.setIdentification(RestParse.toInteger(request.getPersonId(), "personId"));
		Profession profession = new Profession();
		profession.setIdentification(RestParse.toInteger(request.getProfessionId(), "professionId"));
		Study study = new Study();
		study.setPerson(person);
		study.setProfession(profession);
		study.setGraduationDate(RestParse.toDateOrNull(request.getGraduationDate(), "graduationDate"));
		study.setUniversityName(request.getUniversityName());
		return study;
	}
}
