/* 1) Colunas de geo */
ALTER TABLE driver
    ADD COLUMN latitude DECIMAL(10, 8) NULL,
  ADD COLUMN longitude DECIMAL(11,8) NULL,
  ADD COLUMN location_updated_at DATETIME NULL,
  ADD COLUMN location POINT NULL;