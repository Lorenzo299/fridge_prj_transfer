package com.gdserv.fridge.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gdserv.fridge.Category;
import com.gdserv.fridge.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByName(String name);

    List<Product> findByCategory(Category category);

    List<Product> findByExpirationDateBetween(LocalDate startDate, LocalDate endDate);

    List<Product> findByExpirationDateBefore(LocalDate date);

    List<Product> findByExpirationDateAfter(LocalDate date);

    List<Product> findByQuantityEquals(Integer quantity);

    List<Product> findByQuantityGreaterThan(Integer quantity);

}
