package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioModelCli {
	private Integer idProfesion;
	private String profesion;
	private Integer ccPersona;
	private String persona;
	private String fecha;
	private String universidad;
}
