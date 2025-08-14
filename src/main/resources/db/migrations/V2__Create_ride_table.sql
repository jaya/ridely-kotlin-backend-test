CREATE TABLE ride
(
    id              int            NOT NULL AUTO_INCREMENT,
    pick_up         varchar(255)   NOT NULL,
    drop_off        varchar(255)   NOT NULL,
    distance        int            NOT NULL,
    duration        int            NOT NULL,
    status          enum('REQUESTED',
                        'COMPLETED',
                        'IN_PROGRESS',
                        'CANCELLED',
                        'REFUSED') NOT NULL DEFAULT 'REQUESTED',
    price           decimal(10, 2) NULL,
    driver_id       int            NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB