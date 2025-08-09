/* Triggers: mantêm location sempre preenchida (nunca NULL) */
DROP TRIGGER IF EXISTS trg_driver_location_bi;
CREATE TRIGGER trg_driver_location_bi
    BEFORE INSERT ON driver
    FOR EACH ROW
BEGIN
    IF NEW.latitude IS NOT NULL AND NEW.longitude IS NOT NULL THEN
    SET NEW.location = ST_GeomFromText(CONCAT('POINT(', NEW.longitude, ' ', NEW.latitude, ')'), 4326);
    SET NEW.location_updated_at = COALESCE(NEW.location_updated_at, NOW());
    ELSE
    SET NEW.location = ST_GeomFromText('POINT(0 0)', 4326);
    -- deixa location_updated_at como NULL para indicar “sem posição real”
END IF;
END;

DROP TRIGGER IF EXISTS trg_driver_location_bu;
CREATE TRIGGER trg_driver_location_bu
    BEFORE UPDATE ON driver
    FOR EACH ROW
BEGIN
    IF (NEW.latitude <> OLD.latitude) OR (NEW.longitude <> OLD.longitude)
     OR (NEW.latitude IS NULL) OR (NEW.longitude IS NULL) THEN
    IF NEW.latitude IS NOT NULL AND NEW.longitude IS NOT NULL THEN
      SET NEW.location = ST_GeomFromText(CONCAT('POINT(', NEW.longitude, ' ', NEW.latitude, ')'), 4326);
      SET NEW.location_updated_at = NOW();
    ELSE
      SET NEW.location = ST_GeomFromText('POINT(0 0)', 4326);
      SET NEW.location_updated_at = NULL;
END IF;
END IF;
END;