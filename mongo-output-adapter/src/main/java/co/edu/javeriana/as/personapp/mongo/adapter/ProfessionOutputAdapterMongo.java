package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;

import com.mongodb.MongoWriteException;

import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.ProfesionMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.ProfesionRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("professionOutputAdapterMongo")
public class ProfessionOutputAdapterMongo implements ProfessionOutputPort {

	@Autowired
	private ProfesionRepositoryMongo profesionRepositoryMongo;

	@Autowired
	private ProfesionMapperMongo profesionMapperMongo;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public Profession save(Profession profession) {
		log.debug("Into save on Adapter MongoDB");
		try {
			ProfesionDocument persisted = profesionRepositoryMongo.save(profesionMapperMongo.fromDomainToAdapter(profession));
			return profesionMapperMongo.fromAdapterToDomainShallow(persisted);
		} catch (MongoWriteException e) {
			log.warn(e.getMessage());
			return profession;
		}
	}

	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete on Adapter MongoDB");
		mongoTemplate.remove(new BasicQuery(new Document("primaryProfesion", identification)), "estudios");
		profesionRepositoryMongo.deleteById(identification);
		return profesionRepositoryMongo.findById(identification).isEmpty();
	}

	@Override
	public List<Profession> find() {
		log.debug("Into find on Adapter MongoDB");
		return profesionRepositoryMongo.findAll().stream().map(profesionMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Profession findById(Integer identification) {
		log.debug("Into findById on Adapter MongoDB");
		return profesionRepositoryMongo.findById(identification).map(profesionMapperMongo::fromAdapterToDomain)
				.orElse(null);
	}

}
