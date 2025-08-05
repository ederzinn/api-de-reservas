ALTER TABLE reservations
ADD CONSTRAINT fk_user
FOREIGN KEY (user_id) REFERENCES users(id),
ADD CONSTRAINT fk_table
FOREIGN KEY (table_id) REFERENCES restaurant_tables(id);
