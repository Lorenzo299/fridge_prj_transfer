CREATE TABLE fridge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    days_expiration INT NOT NULL,
    expiration_date DATE NOT NULL,
    fridge_id BIGINT,
    CONSTRAINT fk_products_fridge FOREIGN KEY (fridge_id) REFERENCES fridge(id)
);


