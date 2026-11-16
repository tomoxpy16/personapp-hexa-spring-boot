package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.List;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

public class TelefonoMenu extends ModuloMenu {

	private final TelefonoInputAdapterCli adapter;

	public TelefonoMenu(TelefonoInputAdapterCli adapter) {
		this.adapter = adapter;
	}

	@Override
	protected String nombreModulo() {
		return "Telefonos";
	}

	@Override
	protected void seleccionarMotor(String dbOption) throws InvalidOptionException {
		adapter.setPhoneOutputPortInjection(dbOption);
	}

	@Override
	protected List<String> opciones() {
		return List.of("ver todos los telefonos", "buscar un telefono", "crear un telefono", "editar un telefono",
				"eliminar un telefono", "contar telefonos");
	}

	@Override
	protected void ejecutar(int opcion, CliInput in) throws Exception {
		switch (opcion) {
		case 1 -> adapter.historial();
		case 2 -> adapter.buscar(in.readRequired("Numero: "));
		case 3 -> adapter.crear(leerTelefono(in, in.readRequired("Numero: ")));
		case 4 -> adapter.editar(leerTelefono(in, in.readRequired("Numero del telefono a editar: ")));
		case 5 -> adapter.eliminar(in.readRequired("Numero: "));
		case 6 -> adapter.contar();
		default -> System.out.println("La opcion elegida no es valida.");
		}
	}

	private TelefonoModelCli leerTelefono(CliInput in, String numero) {
		TelefonoModelCli model = new TelefonoModelCli();
		model.setNumero(numero);
		model.setOperador(in.readRequired("Operador: "));
		model.setDuenioCc(in.readInt("Cedula del duenio: "));
		return model;
	}
}
