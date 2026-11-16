package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.List;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfesionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

public class ProfesionMenu extends ModuloMenu {

	private final ProfesionInputAdapterCli adapter;

	public ProfesionMenu(ProfesionInputAdapterCli adapter) {
		this.adapter = adapter;
	}

	@Override
	protected String nombreModulo() {
		return "Profesiones";
	}

	@Override
	protected void seleccionarMotor(String dbOption) throws InvalidOptionException {
		adapter.setProfessionOutputPortInjection(dbOption);
	}

	@Override
	protected List<String> opciones() {
		return List.of("ver todas las profesiones", "buscar una profesion", "crear una profesion",
				"editar una profesion", "eliminar una profesion", "contar profesiones",
				"ver estudios de una profesion");
	}

	@Override
	protected void ejecutar(int opcion, CliInput in) throws Exception {
		switch (opcion) {
		case 1 -> adapter.historial();
		case 2 -> adapter.buscar(in.readInt("Id: "));
		case 3 -> adapter.crear(leerProfesion(in, in.readInt("Id: ")));
		case 4 -> adapter.editar(leerProfesion(in, in.readInt("Id de la profesion a editar: ")));
		case 5 -> adapter.eliminar(in.readInt("Id: "));
		case 6 -> adapter.contar();
		case 7 -> adapter.estudios(in.readInt("Id: "));
		default -> System.out.println("La opcion elegida no es valida.");
		}
	}

	private ProfesionModelCli leerProfesion(CliInput in, int id) {
		return new ProfesionModelCli(id, in.readRequired("Nombre: "), in.readLine("Descripcion: "));
	}
}
