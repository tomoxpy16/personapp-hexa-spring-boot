#!/usr/bin/env bash
# Prueba de humo del CRUD completo contra la API REST (MariaDB y MongoDB).
# Uso: bash scripts/test_api.sh [http://localhost:3000]
# Requiere: curl. Crea y luego elimina datos de prueba (persona 111, profesion 99,
# telefono 3990000000), asi que se puede correr varias veces.

BASE="${1:-http://localhost:3000}/api/v1"
PASS=0; FAIL=0

check() { # descripcion, codigo_esperado, metodo, ruta, [body]
  local desc="$1" expected="$2" method="$3" path="$4" body="$5" code
  if [ -n "$body" ]; then
    code=$(curl -s -o /tmp/personapp_resp -w "%{http_code}" -X "$method" "$BASE$path" -H "Content-Type: application/json" -d "$body")
  else
    code=$(curl -s -o /tmp/personapp_resp -w "%{http_code}" -X "$method" "$BASE$path")
  fi
  if [ "$code" = "$expected" ]; then PASS=$((PASS+1)); printf '  OK   %-3s %-6s %-45s %s\n' "$code" "$method" "$path" "$desc"
  else FAIL=$((FAIL+1)); printf '  FAIL %-3s %-6s %-45s %s (esperado %s) %s\n' "$code" "$method" "$path" "$desc" "$expected" "$(cat /tmp/personapp_resp)"; fi
}

for DB in MARIA MONGO; do
  echo "===== $DB ====="
  # --- Persona ---
  check "crear persona" 200 POST   "/persona" "{\"dni\":\"111\",\"firstName\":\"Ana\",\"lastName\":\"Test\",\"age\":\"20\",\"sex\":\"F\",\"database\":\"$DB\"}"
  check "persona duplicada"        409 POST   "/persona" "{\"dni\":\"111\",\"firstName\":\"Ana\",\"lastName\":\"Test\",\"age\":\"20\",\"sex\":\"F\",\"database\":\"$DB\"}"
  check "editar persona"           200 PUT    "/persona/111" "{\"firstName\":\"Ana Maria\",\"lastName\":\"Test\",\"age\":\"21\",\"sex\":\"F\",\"database\":\"$DB\"}"
  check "buscar persona"           200 GET    "/persona/$DB/111"
  check "listar personas"          200 GET    "/persona/$DB"
  # --- Profesion ---
  check "crear profesion" 200 POST   "/profesion" "{\"id\":\"99\",\"name\":\"Tester\",\"description\":\"QA\",\"database\":\"$DB\"}"
  check "editar profesion"         200 PUT    "/profesion/99" "{\"name\":\"QA Engineer\",\"description\":\"Pruebas\",\"database\":\"$DB\"}"
  check "buscar profesion"         200 GET    "/profesion/$DB/99"
  # --- Telefono ---
  check "crear telefono" 200 POST   "/telefono" "{\"number\":\"3990000000\",\"company\":\"Claro\",\"ownerId\":\"111\",\"database\":\"$DB\"}"
  check "telefono duenio inexist." 404 POST   "/telefono" "{\"number\":\"3990000001\",\"company\":\"Claro\",\"ownerId\":\"5\",\"database\":\"$DB\"}"
  check "editar telefono"          200 PUT    "/telefono/3990000000" "{\"company\":\"Tigo\",\"ownerId\":\"111\",\"database\":\"$DB\"}"
  check "telefonos de persona"     200 GET    "/persona/$DB/111/telefonos"
  # --- Estudio ---
  check "crear estudio" 200 POST   "/estudio" "{\"professionId\":\"99\",\"personId\":\"111\",\"graduationDate\":\"2023-05-10\",\"universityName\":\"Javeriana\",\"database\":\"$DB\"}"
  check "estudio prof. inexist."   404 POST   "/estudio" "{\"professionId\":\"12345\",\"personId\":\"111\",\"database\":\"$DB\"}"
  check "editar estudio"           200 PUT    "/estudio/99/111" "{\"graduationDate\":\"2024-01-01\",\"universityName\":\"Andes\",\"database\":\"$DB\"}"
  check "buscar estudio"           200 GET    "/estudio/$DB/99/111"
  check "estudios de persona"      200 GET    "/persona/$DB/111/estudios"
  check "contar estudios"          200 GET    "/estudio/$DB/count"
  # --- Eliminaciones ---
  check "eliminar estudio"         200 DELETE "/estudio/$DB/99/111"
  check "eliminar telefono"        200 DELETE "/telefono/$DB/3990000000"
  check "eliminar profesion"       200 DELETE "/profesion/$DB/99"
  check "eliminar persona"         200 DELETE "/persona/$DB/111"
  check "persona eliminada"        404 GET    "/persona/$DB/111"
  # --- Cascada: borrar persona con telefono y estudio ---
  check "crear persona cascada" 200 POST   "/persona" "{\"dni\":\"112\",\"firstName\":\"Leo\",\"lastName\":\"Test\",\"sex\":\"M\",\"database\":\"$DB\"}"
  check "crear telefono cascada" 200 POST   "/telefono" "{\"number\":\"3990000002\",\"company\":\"Claro\",\"ownerId\":\"112\",\"database\":\"$DB\"}"
  check "crear estudio cascada" 200 POST   "/estudio" "{\"professionId\":\"1\",\"personId\":\"112\",\"database\":\"$DB\"}"
  check "eliminar persona cascada" 200 DELETE "/persona/$DB/112"
  check "telefono borrado cascada" 404 GET    "/telefono/$DB/3990000002"
  check "estudio borrado cascada"  404 GET    "/estudio/$DB/1/112"
done
echo "===== Errores generales ====="
check "BD invalida"               400 GET    "/profesion/ORACLE"
check "dni no numerico"           400 POST   "/persona" "{\"dni\":\"abc\",\"firstName\":\"X\",\"lastName\":\"Y\",\"sex\":\"M\",\"database\":\"MARIA\"}"

echo; echo "Resultado: $PASS OK, $FAIL FAIL"
[ "$FAIL" -eq 0 ]
