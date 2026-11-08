package co.edu.javeriana.as.personapp.controller;

import java.time.LocalDateTime;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

/** Traduce las excepciones del dominio/aplicacion a codigos HTTP. */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

	private ResponseEntity<Response> build(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(new Response(status.name(), message, LocalDateTime.now()));
	}

	@ExceptionHandler(NoExistException.class)
	public ResponseEntity<Response> noExist(NoExistException e) {
		return build(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(AlreadyExistsException.class)
	public ResponseEntity<Response> alreadyExists(AlreadyExistsException e) {
		return build(HttpStatus.CONFLICT, e.getMessage());
	}

	@ExceptionHandler({ InvalidOptionException.class, IllegalArgumentException.class, NullPointerException.class,
			MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Response> badRequest(Exception e) {
		return build(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<Response> dataAccess(DataAccessException e) {
		log.error("Persistence error", e);
		return build(HttpStatus.BAD_REQUEST,
				"Persistence error: " + NestedExceptionUtils.getMostSpecificCause(e).getMessage());
	}
}
