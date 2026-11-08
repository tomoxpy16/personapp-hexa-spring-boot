package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;

import com.mongodb.MongoWriteException;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.PersonaMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.PersonaRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("personOutputAdapterMongo")
public class PersonOutputAdapterMongo implements PersonOutputPort {

	@Autowired
	private PersonaRepositoryMongo personaRepositoryMongo;

	@Autowired
	private PersonaMapperMongo personaMapperMongo;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public Person save(Person person) {
		log.debug("Into save on Adapter MongoDB");
		try {
			PersonaDocument persistedPersona = personaRepositoryMongo.save(personaMapperMongo.fromDomainToAdapter(person));
			return personaMapperMongo.fromAdapterToDomainShallow(persistedPersona);
		} catch (MongoWriteException e) {
			log.warn(e.getMessage());
			return person;
		}
	}

	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete on Adapter MongoDB");
		// Mongo no tiene llaves foraneas: se eliminan en cascada telefonos y estudios
		mongoTemplate.remove(new BasicQuery(new Document("primaryDuenio", identification)), "telefono");
		mongoTemplate.remove(new BasicQuery(new Document("primaryPersona", identification)), "estudios");
		personaRepositoryMongo.deleteById(identification);
		return personaRepositoryMongo.findById(identification).isEmpty();
	}

	@Override
	public List<Person> find() {
		log.debug("Into find on Adapter MongoDB");
		return personaRepositoryMongo.findAll().stream().map(personaMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Person findById(Integer identification) {
		log.debug("Into findById on Adapter MongoDB");
		return personaRepositoryMongo.findById(identification).map(personaMapperMongo::fromAdapterToDomain)
				.orElse(null);
	}

}
