package co.edu.javeriana.as.personapp.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/persona")
public class PersonaControllerV1 {
	
	@Autowired
	private PersonaInputAdapterRest personaInputAdapterRest;
	
	@ResponseBody
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PersonaResponse> personas(@PathVariable String database) {
		log.info("Into personas REST API");
			return personaInputAdapterRest.historial(database.toUpperCase());
	}
	
	@ResponseBody
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse crearPersona(@RequestBody PersonaRequest request) throws AlreadyExistsException {
		log.info("esta en el metodo crearTarea en el controller del api");
		return personaInputAdapterRest.crearPersona(request);
	}

	@ResponseBody
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public Integer contar(@PathVariable String database) throws InvalidOptionException {
		return personaInputAdapterRest.contarPersonas(database);
	}

	@ResponseBody
	@GetMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse persona(@PathVariable String database, @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.buscarPersona(database, cc);
	}

	@ResponseBody
	@GetMapping(path = "/{database}/{cc}/telefonos", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<TelefonoResponse> telefonos(@PathVariable String database, @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.telefonosPersona(database, cc);
	}

	@ResponseBody
	@GetMapping(path = "/{database}/{cc}/estudios", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<EstudioResponse> estudios(@PathVariable String database, @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.estudiosPersona(database, cc);
	}

	@ResponseBody
	@PutMapping(path = "/{cc}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse editarPersona(@PathVariable Integer cc, @RequestBody PersonaRequest request)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.editarPersona(cc, request);
	}

	@ResponseBody
	@DeleteMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarPersona(@PathVariable String database, @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		Boolean ok = personaInputAdapterRest.eliminarPersona(database, cc);
		return new Response(ok ? "OK" : "ERROR", "Persona " + cc + (ok ? " eliminada" : " no eliminada"),
				LocalDateTime.now());
	}
}
