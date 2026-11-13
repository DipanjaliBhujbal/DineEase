ALTER TABLE app_users
    ADD COLUMN restaurant_id BIGINT;

ALTER TABLE app_users
    ADD CONSTRAINT fk_app_users_restaurant
        FOREIGN KEY (restaurant_id)
            REFERENCES restaurants(id)
            ON DELETE RESTRICT;

CREATE INDEX idx_app_users_restaurant_id
    ON app_users(restaurant_id);
