package co.edu.javeriana.as.personapp.mongo.mapper;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;

/**
 * CORRECCION: en la plantilla fromAdapterToDomain retornaba null y mapeaba
 * persona/profesion completas (recursion infinita).
 */
@Mapper
public class EstudiosMapperMongo {

	@Autowired
	private PersonaMapperMongo personaMapperMongo;

	@Autowired
	private ProfesionMapperMongo profesionMapperMongo;

	public static String buildId(Integer professionId, Integer personId) {
		return personId + "-" + professionId;
	}

	public EstudiosDocument fromDomainToAdapter(Study study) {
		EstudiosDocument estudio = new EstudiosDocument();
		estudio.setId(buildId(study.getProfession().getIdentification(), study.getPerson().getIdentification()));
		estudio.setPrimaryPersona(personaMapperMongo.fromDomainToAdapter(study.getPerson()));
		estudio.setPrimaryProfesion(profesionMapperMongo.fromDomainToAdapter(study.getProfession()));
		estudio.setFecha(study.getGraduationDate());
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio;
	}

	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	public Study fromAdapterToDomain(EstudiosDocument estudiosDocument) {
		Study study = new Study();
		study.setPerson(validatePerson(estudiosDocument.getPrimaryPersona()));
		study.setProfession(validateProfession(estudiosDocument.getPrimaryProfesion()));
		study.setGraduationDate(estudiosDocument.getFecha());
		study.setUniversityName(validateUniversityName(estudiosDocument.getUniver()));
		return study;
	}

	private Person validatePerson(PersonaDocument persona) {
		return persona != null ? personaMapperMongo.fromAdapterToDomainShallow(persona) : new Person();
	}

	private Profession validateProfession(ProfesionDocument profesion) {
		return profesion != null ? profesionMapperMongo.fromAdapterToDomainShallow(profesion) : new Profession();
	}

	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}
