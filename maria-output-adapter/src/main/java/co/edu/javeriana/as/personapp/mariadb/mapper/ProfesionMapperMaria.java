package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;

@Mapper
public class ProfesionMapperMaria {

	@Autowired
	private EstudiosMapperMaria estudiosMapperMaria;

	/** Domain -> Adapter. Los estudios se persisten desde su propio adaptador. */
	public ProfesionEntity fromDomainToAdapter(Profession profession) {
		ProfesionEntity profesionEntity = fromDomainToAdapterShallow(profession);
		profesionEntity.setEstudios(new ArrayList<EstudiosEntity>());
		return profesionEntity;
	}

	public ProfesionEntity fromDomainToAdapterShallow(Profession profession) {
		ProfesionEntity profesionEntity = new ProfesionEntity();
		profesionEntity.setId(profession.getIdentification());
		profesionEntity.setNom(profession.getName());
		profesionEntity.setDes(validateDes(profession.getDescription()));
		return profesionEntity;
	}

	private String validateDes(String description) {
		return description != null ? description : "";
	}

	public Profession fromAdapterToDomain(ProfesionEntity profesionEntity) {
		Profession profession = fromAdapterToDomainShallow(profesionEntity);
		profession.setStudies(validateStudies(profesionEntity.getEstudios()));
		return profession;
	}

	public Profession fromAdapterToDomainShallow(ProfesionEntity profesionEntity) {
		Profession profession = new Profession();
		profession.setIdentification(profesionEntity.getId());
		profession.setName(profesionEntity.getNom());
		profession.setDescription(validateDescription(profesionEntity.getDes()));
		return profession;
	}

	private String validateDescription(String des) {
		return des != null ? des : "";
	}

	private List<Study> validateStudies(List<EstudiosEntity> estudiosEntity) {
		return estudiosEntity != null && !estudiosEntity.isEmpty()
				? estudiosEntity.stream().map(estudiosMapperMaria::fromAdapterToDomain).collect(Collectors.toList())
				: new ArrayList<Study>();
	}
}
