package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class StudyUseCase implements StudyInputPort {

	private StudyOutputPort studyPersintence;
	private PersonOutputPort personPersintence;
	private ProfessionOutputPort professionPersintence;

	public StudyUseCase(@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyPersintence,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersintence,
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionPersintence) {
		setPersintence(studyPersintence, personPersintence, professionPersintence);
	}

	@Override
	public void setPersintence(StudyOutputPort studyPersintence, PersonOutputPort personPersintence,
			ProfessionOutputPort professionPersintence) {
		this.studyPersintence = studyPersintence;
		this.personPersintence = personPersintence;
		this.professionPersintence = professionPersintence;
	}

	/** Regla de negocio: la persona y la profesion del estudio deben existir. */
	private void validateReferences(Study study) throws NoExistException {
		Integer personId = study.getPerson().getIdentification();
		Integer professionId = study.getProfession().getIdentification();
		Person person = personId == null ? null : personPersintence.findById(personId);
		if (person == null)
			throw new NoExistException("The person with id " + personId + " does not exist into db");
		Profession profession = professionId == null ? null : professionPersintence.findById(professionId);
		if (profession == null)
			throw new NoExistException("The profession with id " + professionId + " does not exist into db");
		study.setPerson(person);
		study.setProfession(profession);
	}

	@Override
	public Study create(Study study) throws AlreadyExistsException, NoExistException {
		log.debug("Into create on Application Domain");
		validateReferences(study);
		Integer professionId = study.getProfession().getIdentification();
		Integer personId = study.getPerson().getIdentification();
		if (studyPersintence.findById(professionId, personId) != null)
			throw new AlreadyExistsException("The study (profession " + professionId + ", person " + personId
					+ ") already exists into db, cannot be created");
		return studyPersintence.save(study);
	}

	@Override
	public Study edit(Integer professionId, Integer personId, Study study) throws NoExistException {
		Study old = findOne(professionId, personId);
		study.setPerson(old.getPerson());
		study.setProfession(old.getProfession());
		return studyPersintence.save(study);
	}

	@Override
	public Boolean drop(Integer professionId, Integer personId) throws NoExistException {
		findOne(professionId, personId);
		return studyPersintence.delete(professionId, personId);
	}

	@Override
	public List<Study> findAll() {
		log.info("Output: " + studyPersintence.getClass());
		return studyPersintence.find();
	}

	@Override
	public Study findOne(Integer professionId, Integer personId) throws NoExistException {
		Study study = studyPersintence.findById(professionId, personId);
		if (study != null)
			return study;
		throw new NoExistException(
				"The study (profession " + professionId + ", person " + personId + ") does not exist into db");
	}

	@Override
	public Integer count() {
		return findAll().size();
	}
}
