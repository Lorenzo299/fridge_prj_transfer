package com.gdserv.fridge.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.gdserv.fridge.Category;
import com.gdserv.fridge.models.Product;

public interface ProductService {

    // CRUD
    Product getProductById(Long id);

    Product save(Product product);

    void delete(Long id);

    List<Product> getAllProducts();

    // METODI QUERY A DB
    List<Product> getProductsByCategory(Category category);

    List<Product> getProductsByName(String name);

    List<Product> getProductsByExpirationBetween(LocalDate startDate, LocalDate endDate);

    List<Product> getExpiredProductsBefore(LocalDate date);

    List<Product> getProductsByExpirationAfter(LocalDate date);

    List<Product> getProductsByQuantityEquals(Integer quantity);

    List<Product> getProductsByQuantityGreaterThan(Integer quantity);

    // METODI PARLANTI IMPLEMENTANDO LE QUERY
    List<Product> getProductsNearExpiration(Integer days);

    List<Product> getConsumedProducts();

    List<Product> getAvailableProducts();

    // STATISTICHE
    Category getMostConsumedCategory();

    Map<Category, Double> getAverageQuantityPerCategory();

    Map<String, Double> getConsumedAndExpiredPercentage();

    Map<Category, Long> getTotalProductsPerCategory();

    // CONSUMO
    Product consumeProduct(Long productId, int amountToConsume);

}
