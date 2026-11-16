package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;

@Port
public interface StudyInputPort {

	public void setPersintence(StudyOutputPort studyPersintence, PersonOutputPort personPersintence,
			ProfessionOutputPort professionPersintence);

	public Study create(Study study) throws AlreadyExistsException, NoExistException;

	public Study edit(Integer professionId, Integer personId, Study study) throws NoExistException;

	public Boolean drop(Integer professionId, Integer personId) throws NoExistException;

	public List<Study> findAll();

	public Study findOne(Integer professionId, Integer personId) throws NoExistException;

	public Integer count();
}
