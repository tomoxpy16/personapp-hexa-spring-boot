package co.edu.javeriana.as.personapp.terminal.menu;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Lectura de datos por consola. Se usa siempre nextLine() para evitar los
 * problemas de mezclar nextInt() y nextLine() con Scanner.
 */
public class CliInput {

	private final Scanner keyboard;

	public CliInput(Scanner keyboard) {
		this.keyboard = keyboard;
	}

	public String readLine(String prompt) {
		System.out.print(prompt);
		return keyboard.hasNextLine() ? keyboard.nextLine().trim() : "";
	}

	public String readRequired(String prompt) {
		String value;
		do {
			value = readLine(prompt);
			if (value.isEmpty())
				System.out.println("  Este campo es obligatorio.");
		} while (value.isEmpty());
		return value;
	}

	public int readInt(String prompt) {
		while (true) {
			String value = readLine(prompt);
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				System.out.println("  Solo se permiten numeros enteros.");
			}
		}
	}

	/** Entero opcional: Enter vacio retorna null. */
	public Integer readOptionalInt(String prompt) {
		while (true) {
			String value = readLine(prompt);
			if (value.isEmpty())
				return null;
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				System.out.println("  Solo se permiten numeros enteros (o Enter para omitir).");
			}
		}
	}

	/** Fecha opcional yyyy-MM-dd: Enter vacio retorna null. */
	public LocalDate readOptionalDate(String prompt) {
		while (true) {
			String value = readLine(prompt);
			if (value.isEmpty())
				return null;
			try {
				return LocalDate.parse(value);
			} catch (DateTimeParseException e) {
				System.out.println("  Formato invalido, use yyyy-MM-dd (o Enter para omitir).");
			}
		}
	}
}
