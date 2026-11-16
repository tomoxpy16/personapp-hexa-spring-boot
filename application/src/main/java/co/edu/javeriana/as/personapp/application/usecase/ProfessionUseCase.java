package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class ProfessionUseCase implements ProfessionInputPort {

	private ProfessionOutputPort professionPersintence;

	public ProfessionUseCase(
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionPersintence) {
		this.professionPersintence = professionPersintence;
	}

	@Override
	public void setPersintence(ProfessionOutputPort professionPersintence) {
		this.professionPersintence = professionPersintence;
	}

	@Override
	public Profession create(Profession profession) throws AlreadyExistsException {
		log.debug("Into create on Application Domain");
		if (professionPersintence.findById(profession.getIdentification()) != null)
			throw new AlreadyExistsException("The profession with id " + profession.getIdentification()
					+ " already exists into db, cannot be created");
		return professionPersintence.save(profession);
	}

	@Override
	public Profession edit(Integer identification, Profession profession) throws NoExistException {
		findOne(identification);
		profession.setIdentification(identification);
		return professionPersintence.save(profession);
	}

	@Override
	public Boolean drop(Integer identification) throws NoExistException {
		findOne(identification);
		return professionPersintence.delete(identification);
	}

	@Override
	public List<Profession> findAll() {
		log.info("Output: " + professionPersintence.getClass());
		return professionPersintence.find();
	}

	@Override
	public Profession findOne(Integer identification) throws NoExistException {
		Profession profession = professionPersintence.findById(identification);
		if (profession != null)
			return profession;
		throw new NoExistException("The profession with id " + identification + " does not exist into db");
	}

	@Override
	public Integer count() {
		return findAll().size();
	}

	@Override
	public List<Study> getStudies(Integer identification) throws NoExistException {
		return findOne(identification).getStudies();
	}
}
