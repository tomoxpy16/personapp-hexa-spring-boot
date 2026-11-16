package co.edu.javeriana.as.personapp.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(name = "Persona", description = "CRUD de personas. {database} = MARIA | MONGO")
@RequestMapping(path = "/api/v1/persona", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonaControllerV1 {

	@Autowired
	private PersonaInputAdapterRest personaInputAdapterRest;

	@Operation(summary = "Listar personas")
	@GetMapping("/{database}")
	public List<PersonaResponse> personas(@PathVariable String database) throws InvalidOptionException {
		log.info("Into personas REST API");
		return personaInputAdapterRest.historial(database);
	}

	@Operation(summary = "Contar personas")
	@GetMapping("/{database}/count")
	public Integer contar(@PathVariable String database) throws InvalidOptionException {
		return personaInputAdapterRest.contarPersonas(database);
	}

	@Operation(summary = "Buscar persona por cedula")
	@GetMapping("/{database}/{cc}")
	public PersonaResponse persona(@PathVariable String database, @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.buscarPersona(database, cc);
	}

	@Operation(summary = "Telefonos de una persona")
	@GetMapping("/{database}/{cc}/telefonos")
	public List<TelefonoResponse> telefonos(@PathVariable String database, @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.telefonosPersona(database, cc);
	}

	@Operation(summary = "Crear persona (database va en el body)")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse crearPersona(@RequestBody PersonaRequest request)
			throws InvalidOptionException, AlreadyExistsException {
		log.info("Into crearPersona REST API");
		return personaInputAdapterRest.crearPersona(request);
	}

	@Operation(summary = "Editar persona (database va en el body)")
	@PutMapping(path = "/{cc}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse editarPersona(@PathVariable Integer cc, @RequestBody PersonaRequest request)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.editarPersona(cc, request);
	}

	@Operation(summary = "Eliminar persona (y en cascada sus telefonos y estudios)")
	@DeleteMapping("/{database}/{cc}")
	public Response eliminarPersona(@PathVariable String database, @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		Boolean ok = personaInputAdapterRest.eliminarPersona(database, cc);
		return new Response(ok ? "OK" : "ERROR", "Persona " + cc + (ok ? " eliminada" : " no eliminada"),
				LocalDateTime.now());
	}
}
