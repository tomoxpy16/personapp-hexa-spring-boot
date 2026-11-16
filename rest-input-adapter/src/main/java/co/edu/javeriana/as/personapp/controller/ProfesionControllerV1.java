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
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.ProfesionInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Profesion", description = "CRUD de profesiones. {database} = MARIA | MONGO")
@RequestMapping(path = "/api/v1/profesion", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfesionControllerV1 {

	@Autowired
	private ProfesionInputAdapterRest profesionInputAdapterRest;

	@Operation(summary = "Listar profesiones")
	@GetMapping("/{database}")
	public List<ProfesionResponse> profesiones(@PathVariable String database) throws InvalidOptionException {
		return profesionInputAdapterRest.historial(database);
	}

	@Operation(summary = "Contar profesiones")
	@GetMapping("/{database}/count")
	public Integer contar(@PathVariable String database) throws InvalidOptionException {
		return profesionInputAdapterRest.contar(database);
	}

	@Operation(summary = "Buscar profesion por id")
	@GetMapping("/{database}/{id}")
	public ProfesionResponse profesion(@PathVariable String database, @PathVariable Integer id)
			throws InvalidOptionException, NoExistException {
		return profesionInputAdapterRest.buscar(database, id);
	}

	@Operation(summary = "Estudios asociados a una profesion")
	@GetMapping("/{database}/{id}/estudios")
	public List<EstudioResponse> estudios(@PathVariable String database, @PathVariable Integer id)
			throws InvalidOptionException, NoExistException {
		return profesionInputAdapterRest.estudios(database, id);
	}

	@Operation(summary = "Crear profesion (database va en el body)")
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ProfesionResponse crear(@RequestBody ProfesionRequest request)
			throws InvalidOptionException, AlreadyExistsException {
		return profesionInputAdapterRest.crear(request);
	}

	@Operation(summary = "Editar profesion (database va en el body)")
	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ProfesionResponse editar(@PathVariable Integer id, @RequestBody ProfesionRequest request)
			throws InvalidOptionException, NoExistException {
		return profesionInputAdapterRest.editar(id, request);
	}

	@Operation(summary = "Eliminar profesion (y en cascada sus estudios)")
	@DeleteMapping("/{database}/{id}")
	public Response eliminar(@PathVariable String database, @PathVariable Integer id)
			throws InvalidOptionException, NoExistException {
		Boolean ok = profesionInputAdapterRest.eliminar(database, id);
		return new Response(ok ? "OK" : "ERROR", "Profesion " + id + (ok ? " eliminada" : " no eliminada"),
				LocalDateTime.now());
	}
}
