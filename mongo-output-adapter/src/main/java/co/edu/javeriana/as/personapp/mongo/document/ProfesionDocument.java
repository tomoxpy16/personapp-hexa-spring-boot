package co.edu.javeriana.as.personapp.mongo.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("profesion")
public class ProfesionDocument {
	@Id
	private Integer id;
	private String nom;
	private String des;
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@DocumentReference(lazy = true, lookup = "{ 'primaryProfesion' : ?#{#self._id} }")
	@ReadOnlyProperty
	private List<EstudiosDocument> estudios;
}
