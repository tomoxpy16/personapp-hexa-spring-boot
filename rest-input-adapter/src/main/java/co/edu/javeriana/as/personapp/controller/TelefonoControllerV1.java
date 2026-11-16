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

import co.edu.javeriana.as.personapp.adapter.TelefonoInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Telefono", description = "CRUD de telefonos. {database} = MARIA | MONGO")
@RequestMapping(path = "/api/v1/telefono", produces = MediaType.APPLICATION_JSON_VALUE)
public class TelefonoControllerV1 {

	@Autowired
	private TelefonoInputAdapterRest telefonoInputAdapterRest;

	@Operation(summary = "Listar telefonos")
	@GetMapping("/{database}")
	public List<TelefonoResponse> telefonos(@PathVariable String database) throws InvalidOptionException {
		return telefonoInputAdapterRest.historial(database);
	}

	@Operation(summary = "Contar telefonos")
	@GetMapping("/{database}/count")
	public Integer contar(@PathVariable String database) throws InvalidOptionException {
		return telefonoInputAdapterRest.contar(database);
	}

	@Operation(summary = "Buscar telefono por numero")
	@GetMapping("/{database}/{number}")
	public TelefonoResponse telefono(@PathVariable String database, @PathVariable String number)
			throws InvalidOptionException, NoExistException {
		return telefonoInputAdapterRest.buscar(database, number);
	}

	@Operation(summary = "Crear telefono (database va en el body; ownerId debe existir)")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public TelefonoResponse crear(@RequestBody TelefonoRequest request)
			throws InvalidOptionException, AlreadyExistsException, NoExistException {
		return telefonoInputAdapterRest.crear(request);
	}

	@Operation(summary = "Editar telefono (database va en el body)")
	@PutMapping(path = "/{number}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public TelefonoResponse editar(@PathVariable String number, @RequestBody TelefonoRequest request)
			throws InvalidOptionException, NoExistException {
		return telefonoInputAdapterRest.editar(number, request);
	}

	@Operation(summary = "Eliminar telefono")
	@DeleteMapping("/{database}/{number}")
	public Response eliminar(@PathVariable String database, @PathVariable String number)
			throws InvalidOptionException, NoExistException {
		Boolean ok = telefonoInputAdapterRest.eliminar(database, number);
		return new Response(ok ? "OK" : "ERROR", "Telefono " + number + (ok ? " eliminado" : " no eliminado"),
				LocalDateTime.now());
	}
}
