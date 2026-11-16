package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.List;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;

public class PersonaMenu extends ModuloMenu {

	private final PersonaInputAdapterCli adapter;

	public PersonaMenu(PersonaInputAdapterCli adapter) {
		this.adapter = adapter;
	}

	@Override
	protected String nombreModulo() {
		return "Personas";
	}

	@Override
	protected void seleccionarMotor(String dbOption) throws InvalidOptionException {
		adapter.setPersonOutputPortInjection(dbOption);
	}

	@Override
	protected List<String> opciones() {
		return List.of("ver todas las personas", "buscar una persona", "crear una persona", "editar una persona",
				"eliminar una persona", "contar personas", "ver telefonos de una persona",
				"ver estudios de una persona");
	}

	@Override
	protected void ejecutar(int opcion, CliInput in) throws Exception {
		switch (opcion) {
		case 1 -> adapter.historial();
		case 2 -> adapter.buscar(in.readInt("Cedula: "));
		case 3 -> adapter.crear(leerPersona(in, in.readInt("Cedula: ")));
		case 4 -> adapter.editar(leerPersona(in, in.readInt("Cedula de la persona a editar: ")));
		case 5 -> adapter.eliminar(in.readInt("Cedula: "));
		case 6 -> adapter.contar();
		case 7 -> adapter.telefonos(in.readInt("Cedula: "));
		case 8 -> adapter.estudios(in.readInt("Cedula: "));
		default -> System.out.println("La opcion elegida no es valida.");
		}
	}

	private PersonaModelCli leerPersona(CliInput in, int cc) {
		PersonaModelCli model = new PersonaModelCli();
		model.setCc(cc);
		model.setNombre(in.readRequired("Nombre: "));
		model.setApellido(in.readRequired("Apellido: "));
		model.setGenero(in.readRequired("Genero (M/F): "));
		model.setEdad(in.readOptionalInt("Edad (Enter para omitir): "));
		return model;
	}
}
