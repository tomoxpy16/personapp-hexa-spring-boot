package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

@Mapper
public class ProfesionMapperCli {

	public ProfesionModelCli fromDomainToAdapterCli(Profession profession) {
		return new ProfesionModelCli(profession.getIdentification(), profession.getName(),
				profession.getDescription());
	}

	public Profession fromAdapterCliToDomain(ProfesionModelCli model) {
		Profession profession = new Profession();
		profession.setIdentification(model.getId());
		profession.setName(model.getNombre());
		profession.setDescription(model.getDescripcion());
		return profession;
	}
}
