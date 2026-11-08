package co.edu.javeriana.as.personapp.mariadb.mapper;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;

/**
 * CORRECCION: en la plantilla fromAdapterToDomain retornaba null y mapeaba
 * persona/profesion completas (recursion infinita). Ahora retorna el Study y
 * usa referencias shallow.
 */
@Mapper
public class EstudiosMapperMaria {

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

	@Autowired
	private ProfesionMapperMaria profesionMapperMaria;

	public EstudiosEntity fromDomainToAdapter(Study study) {
		EstudiosEntityPK estudioPK = new EstudiosEntityPK();
		estudioPK.setCcPer(study.getPerson().getIdentification());
		estudioPK.setIdProf(study.getProfession().getIdentification());
		EstudiosEntity estudio = new EstudiosEntity();
		estudio.setEstudiosPK(estudioPK);
		estudio.setFecha(study.getGraduationDate());
		estudio.setUniver(validateUniver(study.getUniversityName()));
		estudio.setPersona(personaMapperMaria.fromDomainToAdapterShallow(study.getPerson()));
		estudio.setProfesion(profesionMapperMaria.fromDomainToAdapterShallow(study.getProfession()));
		return estudio;
	}

	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	public Study fromAdapterToDomain(EstudiosEntity estudiosEntity) {
		Study study = new Study();
		study.setPerson(validatePerson(estudiosEntity.getPersona()));
		study.setProfession(validateProfession(estudiosEntity.getProfesion()));
		study.setGraduationDate(estudiosEntity.getFecha());
		study.setUniversityName(validateUniversityName(estudiosEntity.getUniver()));
		return study;
	}

	private Person validatePerson(PersonaEntity persona) {
		return persona != null ? personaMapperMaria.fromAdapterToDomainShallow(persona) : new Person();
	}

	private Profession validateProfession(ProfesionEntity profesion) {
		return profesion != null ? profesionMapperMaria.fromAdapterToDomainShallow(profesion) : new Profession();
	}

	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}
