CREATE TABLE driver
(
    id                int          NOT NULL AUTO_INCREMENT,
    name              varchar(255) NOT NULL,
    activation_date   datetime     NOT NULL,
    car_license_plate varchar(255) NOT NULL,
    car_model         varchar(255) NOT NULL,
    car_color         varchar(255) NOT NULL,
    available         tinyint      NOT NULL,
    latitude          double       NOT NULL,
    longitude         double       NOT NULL,
    city              varchar(255) NOT NULL,
    sublocality       varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB