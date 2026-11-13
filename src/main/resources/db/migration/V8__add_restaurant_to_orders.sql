ALTER TABLE customer_orders
    ADD COLUMN restaurant_id BIGINT;

UPDATE customer_orders
SET restaurant_id = 1
WHERE restaurant_id IS NULL;

ALTER TABLE customer_orders
    ALTER COLUMN restaurant_id SET NOT NULL;

ALTER TABLE customer_orders
    ADD CONSTRAINT fk_customer_orders_restaurant
        FOREIGN KEY (restaurant_id)
            REFERENCES restaurants(id)
            ON DELETE RESTRICT;

CREATE INDEX idx_customer_orders_restaurant_id
    ON customer_orders(restaurant_id);