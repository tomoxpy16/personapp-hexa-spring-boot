package co.edu.javeriana.as.personapp.terminal.menu;

import java.time.LocalDate;
import java.util.List;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.EstudioInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

public class EstudioMenu extends ModuloMenu {

	private final EstudioInputAdapterCli adapter;

	public EstudioMenu(EstudioInputAdapterCli adapter) {
		this.adapter = adapter;
	}

	@Override
	protected String nombreModulo() {
		return "Estudios";
	}

	@Override
	protected void seleccionarMotor(String dbOption) throws InvalidOptionException {
		adapter.setStudyOutputPortInjection(dbOption);
	}

	@Override
	protected List<String> opciones() {
		return List.of("ver todos los estudios", "buscar un estudio", "crear un estudio", "editar un estudio",
				"eliminar un estudio", "contar estudios");
	}

	@Override
	protected void ejecutar(int opcion, CliInput in) throws Exception {
		switch (opcion) {
		case 1 -> adapter.historial();
		case 2 -> adapter.buscar(in.readInt("Id de la profesion: "), in.readInt("Cedula de la persona: "));
		case 3 -> adapter.crear(leerEstudio(in));
		case 4 -> adapter.editar(leerEstudio(in));
		case 5 -> adapter.eliminar(in.readInt("Id de la profesion: "), in.readInt("Cedula de la persona: "));
		case 6 -> adapter.contar();
		default -> System.out.println("La opcion elegida no es valida.");
		}
	}

	private EstudioModelCli leerEstudio(CliInput in) {
		EstudioModelCli model = new EstudioModelCli();
		model.setIdProfesion(in.readInt("Id de la profesion: "));
		model.setCcPersona(in.readInt("Cedula de la persona: "));
		LocalDate fecha = in.readOptionalDate("Fecha de grado yyyy-MM-dd (Enter para omitir): ");
		model.setFecha(fecha != null ? fecha.toString() : null);
		model.setUniversidad(in.readLine("Universidad: "));
		return model;
	}
}
