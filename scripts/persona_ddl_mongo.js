// DDL MongoDB: usuario de aplicacion y colecciones.
// Ejecutar con: mongosh "mongodb://root:root@localhost:27017/admin" persona_ddl_mongo.js
// (se usa getSiblingDB en vez de "use" para que funcione tambien como script)

const admin = db.getSiblingDB("admin");

if (admin.getUser("persona_db") !== null) {
  admin.dropUser("persona_db");
}

admin.createUser({
  user: "persona_db",
  pwd: "persona_db",
  roles: [
    { role: "read", db: "persona_db" },
    { role: "readWrite", db: "persona_db" },
    { role: "dbAdmin", db: "persona_db" }
  ],
  mechanisms: ["SCRAM-SHA-1", "SCRAM-SHA-256"]
});

const personaDb = db.getSiblingDB("persona_db");
personaDb.dropDatabase();
["persona", "profesion", "telefono", "estudios"].forEach(c => personaDb.createCollection(c));
