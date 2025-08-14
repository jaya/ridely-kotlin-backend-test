ALTER TABLE ride
    MODIFY COLUMN driver_id BIGINT,
    ADD COLUMN passenger_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_ride_driver FOREIGN KEY (driver_id) REFERENCES driver(id),
    ADD CONSTRAINT fk_ride_passenger FOREIGN KEY (passenger_id) REFERENCES passenger(id);