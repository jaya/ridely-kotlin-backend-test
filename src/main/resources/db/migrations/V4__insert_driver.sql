INSERT INTO driver (
    id, name, activation_date, car_license_plate, car_model, car_color, available,
    latitude, longitude, location_updated_at, location
) VALUES
      (1, 'Ana Silva',   '2024-01-10', 'ABC1A23', 'Fiesta',    'Branco', 1, -26.923026, -49.063412, NOW(), ST_GeomFromText('POINT(-49.063412 -26.923026)', 4326)),
      (2, 'Bruno Souza', '2024-02-15', 'DEF4B56', 'Onix',      'Preto',  1, -26.920000, -49.070000, NOW(), ST_GeomFromText('POINT(-49.070000 -26.920000)', 4326)),
      (3, 'Carla Lima',  '2024-03-20', 'GHI7C89', 'Corolla',   'Prata',  1, -26.925500, -49.060500, NOW(), ST_GeomFromText('POINT(-49.060500 -26.925500)', 4326)),
      (4, 'Diego Alves', '2024-04-25', 'JKL0D12', 'HB20',      'Vermelho', 1, -26.940000, -49.050000, NOW(), ST_GeomFromText('POINT(-49.050000 -26.940000)', 4326)),
      (5, 'Eva Torres',  '2024-05-30', 'MNO3E45', 'Civic',     'Azul',   1, -26.915000, -49.080000, NOW(), ST_GeomFromText('POINT(-49.080000 -26.915000)', 4326));
