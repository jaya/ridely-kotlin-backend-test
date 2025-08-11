/* 1) Colunas de geo */
ALTER TABLE driver
    ADD COLUMN latitude double not NULL,
  ADD COLUMN longitude double not NULL,
  ADD COLUMN location_updated_at DATETIME NULL,
  ADD COLUMN location POINT NULL SRID 4326;