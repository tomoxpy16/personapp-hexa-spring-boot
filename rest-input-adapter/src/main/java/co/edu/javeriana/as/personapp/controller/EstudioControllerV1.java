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

import co.edu.javeriana.as.personapp.adapter.EstudioInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Estudio", description = "CRUD de estudios (llave compuesta profesion + persona). {database} = MARIA | MONGO")
@RequestMapping(path = "/api/v1/estudio", produces = MediaType.APPLICATION_JSON_VALUE)
public class EstudioControllerV1 {

	@Autowired
	private EstudioInputAdapterRest estudioInputAdapterRest;

	@Operation(summary = "Listar estudios")
	@GetMapping("/{database}")
	public List<EstudioResponse> estudios(@PathVariable String database) throws InvalidOptionException {
		return estudioInputAdapterRest.historial(database);
	}

	@Operation(summary = "Contar estudios")
	@GetMapping("/{database}/count")
	public Integer contar(@PathVariable String database) throws InvalidOptionException {
		return estudioInputAdapterRest.contar(database);
	}

	@Operation(summary = "Buscar estudio por (profesion, persona)")
	@GetMapping("/{database}/{professionId}/{personId}")
	public EstudioResponse estudio(@PathVariable String database, @PathVariable Integer professionId,
			@PathVariable Integer personId) throws InvalidOptionException, NoExistException {
		return estudioInputAdapterRest.buscar(database, professionId, personId);
	}

	@Operation(summary = "Crear estudio (database va en el body; persona y profesion deben existir)")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public EstudioResponse crear(@RequestBody EstudioRequest request)
			throws InvalidOptionException, AlreadyExistsException, NoExistException {
		return estudioInputAdapterRest.crear(request);
	}

	@Operation(summary = "Editar estudio (fecha y universidad; database va en el body)")
	@PutMapping(path = "/{professionId}/{personId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public EstudioResponse editar(@PathVariable Integer professionId, @PathVariable Integer personId,
			@RequestBody EstudioRequest request) throws InvalidOptionException, NoExistException {
		return estudioInputAdapterRest.editar(professionId, personId, request);
	}

	@Operation(summary = "Eliminar estudio")
	@DeleteMapping("/{database}/{professionId}/{personId}")
	public Response eliminar(@PathVariable String database, @PathVariable Integer professionId,
			@PathVariable Integer personId) throws InvalidOptionException, NoExistException {
		Boolean ok = estudioInputAdapterRest.eliminar(database, professionId, personId);
		return new Response(ok ? "OK" : "ERROR",
				"Estudio (" + professionId + ", " + personId + ")" + (ok ? " eliminado" : " no eliminado"),
				LocalDateTime.now());
	}
}
