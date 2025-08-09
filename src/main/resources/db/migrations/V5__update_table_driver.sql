/* Backfill da coluna location (evita NOT NULL falhar)
      - Com lat/lon válidos, grava o ponto correto (SRID 4326)
      - Sem lat/lon, grava POINT(0 0) (marcaremos como “sem posição” usando location_updated_at IS NULL)
*/
UPDATE driver
SET location = ST_GeomFromText(CONCAT('POINT(', longitude, ' ', latitude, ')'), 4326)
WHERE latitude  IS NOT NULL
  AND longitude IS NOT NULL;

UPDATE driver
SET location = ST_GeomFromText('POINT(0 0)', 4326)
WHERE location IS NULL;