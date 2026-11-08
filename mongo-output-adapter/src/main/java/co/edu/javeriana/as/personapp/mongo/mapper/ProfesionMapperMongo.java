package co.edu.javeriana.as.personapp.mongo.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;

@Mapper
public class ProfesionMapperMongo {

	@Autowired
	private EstudiosMapperMongo estudiosMapperMongo;

	public ProfesionDocument fromDomainToAdapter(Profession profession) {
		ProfesionDocument profesionDocument = new ProfesionDocument();
		profesionDocument.setId(profession.getIdentification());
		profesionDocument.setNom(profession.getName());
		profesionDocument.setDes(validateDes(profession.getDescription()));
		return profesionDocument;
	}

	private String validateDes(String description) {
		return description != null ? description : "";
	}

	public Profession fromAdapterToDomain(ProfesionDocument profesionDocument) {
		Profession profession = fromAdapterToDomainShallow(profesionDocument);
		profession.setStudies(validateStudies(profesionDocument.getEstudios()));
		return profession;
	}

	public Profession fromAdapterToDomainShallow(ProfesionDocument profesionDocument) {
		Profession profession = new Profession();
		profession.setIdentification(profesionDocument.getId());
		profession.setName(profesionDocument.getNom());
		profession.setDescription(validateDescription(profesionDocument.getDes()));
		return profession;
	}

	private String validateDescription(String des) {
		return des != null ? des : "";
	}

	private List<Study> validateStudies(List<EstudiosDocument> estudiosDocument) {
		return estudiosDocument != null && !estudiosDocument.isEmpty()
				? estudiosDocument.stream().map(estudiosMapperMongo::fromAdapterToDomain).collect(Collectors.toList())
				: new ArrayList<Study>();
	}
}
