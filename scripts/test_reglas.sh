#!/usr/bin/env bash
# Prueba de reglas de negocio: cada caso invalido debe ser rechazado (4xx) y no guardarse.
# Uso: bash scripts/test_reglas.sh [http://localhost:3000]   (resultado esperado: 0 aceptados)
B="${1:-http://localhost:3000}/api/v1"
OUT="$(mktemp)"; OK=0; MAL=0
post() { curl -s -o "$OUT" -w "%{http_code}" -X "$1" "$B$2" -H "Content-Type: application/json" -d "$3"; }
caso() { # descripcion metodo ruta body
  local code; code=$(post "$2" "$3" "$4")
  if [[ "$code" =~ ^4 ]]; then OK=$((OK+1)); printf '  RECHAZA %s  %s\n' "$code" "$1"
  else MAL=$((MAL+1)); printf '  ACEPTA  %s  %s   -> %s\n' "$code" "$1" "$(head -c 140 "$OUT")"; fi
}
limpiar() { for db in MARIA MONGO; do
  for n in 12345 300ABC4567 3001234 30012345678901 3000000001 3000000002; do curl -s -o /dev/null -X DELETE "$B/telefono/$db/$n"; done
  for c in -5 0 801 802 803 804 805 806 807; do curl -s -o /dev/null -X DELETE "$B/persona/$db/$c"; done
  for p in -3 0 9001 9002; do curl -s -o /dev/null -X DELETE "$B/profesion/$db/$p"; done
done; }
limpiar
for DB in MARIA MONGO; do
  echo "===== $DB ====="
  echo "-- Persona"
  caso "edad negativa"                 POST /persona "{\"dni\":\"801\",\"firstName\":\"A\",\"lastName\":\"B\",\"age\":\"-5\",\"sex\":\"M\",\"database\":\"$DB\"}"
  caso "edad absurda (500)"            POST /persona "{\"dni\":\"802\",\"firstName\":\"A\",\"lastName\":\"B\",\"age\":\"500\",\"sex\":\"M\",\"database\":\"$DB\"}"
  caso "cedula negativa"               POST /persona "{\"dni\":\"-5\",\"firstName\":\"A\",\"lastName\":\"B\",\"sex\":\"M\",\"database\":\"$DB\"}"
  caso "cedula cero"                   POST /persona "{\"dni\":\"0\",\"firstName\":\"A\",\"lastName\":\"B\",\"sex\":\"M\",\"database\":\"$DB\"}"
  caso "genero invalido (X)"           POST /persona "{\"dni\":\"803\",\"firstName\":\"A\",\"lastName\":\"B\",\"sex\":\"X\",\"database\":\"$DB\"}"
  caso "nombre vacio"                  POST /persona "{\"dni\":\"804\",\"firstName\":\"  \",\"lastName\":\"B\",\"sex\":\"M\",\"database\":\"$DB\"}"
  caso "nombre > 45 caracteres"        POST /persona "{\"dni\":\"805\",\"firstName\":\"$(printf 'a%.0s' {1..50})\",\"lastName\":\"B\",\"sex\":\"M\",\"database\":\"$DB\"}"
  caso "editar con edad negativa"      PUT  /persona/123456789 "{\"firstName\":\"Pepe\",\"lastName\":\"Perez\",\"age\":\"-1\",\"sex\":\"M\",\"database\":\"$DB\"}"
  echo "-- Telefono"
  caso "telefono de 5 digitos"         POST /telefono "{\"number\":\"12345\",\"company\":\"Claro\",\"ownerId\":\"123456789\",\"database\":\"$DB\"}"
  caso "telefono de 7 digitos"         POST /telefono "{\"number\":\"3001234\",\"company\":\"Claro\",\"ownerId\":\"123456789\",\"database\":\"$DB\"}"
  caso "telefono de 14 digitos"        POST /telefono "{\"number\":\"30012345678901\",\"company\":\"Claro\",\"ownerId\":\"123456789\",\"database\":\"$DB\"}"
  caso "telefono con letras"           POST /telefono "{\"number\":\"300ABC4567\",\"company\":\"Claro\",\"ownerId\":\"123456789\",\"database\":\"$DB\"}"
  caso "operador vacio"                POST /telefono "{\"number\":\"3000000001\",\"company\":\" \",\"ownerId\":\"123456789\",\"database\":\"$DB\"}"
  echo "-- Profesion"
  caso "id negativo"                   POST /profesion "{\"id\":\"-3\",\"name\":\"X\",\"database\":\"$DB\"}"
  caso "id cero"                       POST /profesion "{\"id\":\"0\",\"name\":\"X\",\"database\":\"$DB\"}"
  caso "nombre vacio"                  POST /profesion "{\"id\":\"9001\",\"name\":\" \",\"database\":\"$DB\"}"
  caso "nombre > 90 caracteres"        POST /profesion "{\"id\":\"9002\",\"name\":\"$(printf 'a%.0s' {1..95})\",\"database\":\"$DB\"}"
  echo "-- Estudio"
  caso "fecha de grado futura"         POST /estudio "{\"professionId\":\"3\",\"personId\":\"987654321\",\"graduationDate\":\"2099-01-01\",\"database\":\"$DB\"}"
  caso "universidad > 50 caracteres"   POST /estudio "{\"professionId\":\"4\",\"personId\":\"987654321\",\"universityName\":\"$(printf 'u%.0s' {1..60})\",\"database\":\"$DB\"}"
  caso "fecha con formato invalido"    POST /estudio "{\"professionId\":\"2\",\"personId\":\"987654321\",\"graduationDate\":\"01/01/2020\",\"database\":\"$DB\"}"
  curl -s -o /dev/null -X DELETE "$B/estudio/$DB/3/987654321"; curl -s -o /dev/null -X DELETE "$B/estudio/$DB/4/987654321"; curl -s -o /dev/null -X DELETE "$B/estudio/$DB/2/987654321"
  curl -s -o /dev/null -X PUT "$B/persona/123456789" -H "Content-Type: application/json" -d "{\"firstName\":\"Pepe\",\"lastName\":\"Perez\",\"age\":\"30\",\"sex\":\"M\",\"database\":\"$DB\"}"
done
limpiar
echo; echo "Rechazados correctamente: $OK | Aceptados (falla de validacion): $MAL"
[ "$MAL" -eq 0 ]
