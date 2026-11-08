// DML MongoDB. CORRECCION: la plantilla usaba "use prueba_db" (BD equivocada).
// Las relaciones (@DocumentReference) guardan solo el _id del documento referenciado.
// Las fechas van a las 12:00 UTC para que no cambie el dia al convertir a LocalDate.

const personaDb = db.getSiblingDB("persona_db");

personaDb.persona.insertMany([
	{ "_id": NumberInt(123456789), "nombre": "Pepe", "apellido": "Perez", "genero": "M", "edad": NumberInt(30),
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument" },
	{ "_id": NumberInt(987654321), "nombre": "Pepito", "apellido": "Perez", "genero": "M",
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument" },
	{ "_id": NumberInt(321654987), "nombre": "Pepa", "apellido": "Juarez", "genero": "F", "edad": NumberInt(30),
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument" },
	{ "_id": NumberInt(147258369), "nombre": "Pepita", "apellido": "Juarez", "genero": "F", "edad": NumberInt(10),
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument" },
	{ "_id": NumberInt(963852741), "nombre": "Fede", "apellido": "Perez", "genero": "M", "edad": NumberInt(18),
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument" }
], { ordered: false });

personaDb.profesion.insertMany([
	{ "_id": NumberInt(1), "nom": "Ingeniero de Sistemas", "des": "Disena, desarrolla y mantiene sistemas de software",
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument" },
	{ "_id": NumberInt(2), "nom": "Medico", "des": "Diagnostica y trata enfermedades",
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument" },
	{ "_id": NumberInt(3), "nom": "Abogado", "des": "Asesora y representa en asuntos legales",
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument" },
	{ "_id": NumberInt(4), "nom": "Arquitecto", "des": "Disena edificaciones y espacios",
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument" }
], { ordered: false });

personaDb.telefono.insertMany([
	{ "_id": "3001234567", "oper": "Claro", "primaryDuenio": NumberInt(123456789),
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" },
	{ "_id": "3109876543", "oper": "Movistar", "primaryDuenio": NumberInt(123456789),
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" },
	{ "_id": "3204567890", "oper": "Tigo", "primaryDuenio": NumberInt(321654987),
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" },
	{ "_id": "3157894561", "oper": "Claro", "primaryDuenio": NumberInt(963852741),
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" }
], { ordered: false });

// _id del estudio = "<cc_persona>-<id_profesion>"
personaDb.estudios.insertMany([
	{ "_id": "123456789-1", "primaryPersona": NumberInt(123456789), "primaryProfesion": NumberInt(1),
	  "fecha": ISODate("2015-12-01T12:00:00Z"), "univer": "Pontificia Universidad Javeriana",
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument" },
	{ "_id": "123456789-4", "primaryPersona": NumberInt(123456789), "primaryProfesion": NumberInt(4),
	  "fecha": ISODate("2020-06-15T12:00:00Z"), "univer": "Universidad Nacional de Colombia",
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument" },
	{ "_id": "321654987-2", "primaryPersona": NumberInt(321654987), "primaryProfesion": NumberInt(2),
	  "fecha": ISODate("2018-11-30T12:00:00Z"), "univer": "Universidad de los Andes",
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument" },
	{ "_id": "963852741-3", "primaryPersona": NumberInt(963852741), "primaryProfesion": NumberInt(3),
	  "fecha": ISODate("2024-07-20T12:00:00Z"), "univer": "Universidad del Rosario",
	  "_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument" }
], { ordered: false });
