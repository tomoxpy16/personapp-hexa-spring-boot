package co.edu.javeriana.as.personapp.adapter;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;

/** Traduce el texto recibido (MARIA, MARIADB, MONGO, MONGODB) a DatabaseOption. */
public final class DatabaseSelector {

	private DatabaseSelector() {
	}

	public static DatabaseOption parse(String database) throws InvalidOptionException {
		String db = database == null ? "" : database.trim().toUpperCase();
		if (db.equals("MARIA") || db.equals("MARIADB"))
			return DatabaseOption.MARIA;
		if (db.equals("MONGO") || db.equals("MONGODB"))
			return DatabaseOption.MONGO;
		throw new InvalidOptionException("Invalid database option: " + database + " (use MARIA or MONGO)");
	}

	public static String label(DatabaseOption option) {
		return option == DatabaseOption.MARIA ? "MariaDB" : "MongoDB";
	}
}
