package com.gdserv.fridge.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gdserv.fridge.Category;
import com.gdserv.fridge.models.Product;
import com.gdserv.fridge.services.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    // CRUD BASE
    @PostMapping
    public Product saveProduct(@RequestBody Product product) {
        return productService.save(product);
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // QUERY DIRETTE

    @GetMapping("/by-name")
    public List<Product> getByName(@RequestParam String name) {
        return productService.getProductsByName(name);
    }

    @GetMapping("/by-category")
    public List<Product> getByCategory(@RequestParam Category category) {
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/expiration/before")
    public List<Product> getExpiredBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return productService.getExpiredProductsBefore(date);
    }

    @GetMapping("/expiration/after")
    public List<Product> getExpirationAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return productService.getProductsByExpirationAfter(date);
    }

    @GetMapping("/expiration/between")
    public List<Product> getExpirationBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return productService.getProductsByExpirationBetween(startDate, endDate);
    }

    @GetMapping("/quantity/equals")
    public List<Product> getByQuantityEquals(@RequestParam Integer quantity) {
        return productService.getProductsByQuantityEquals(quantity);
    }

    @GetMapping("/quantity/greater-than")
    public List<Product> getByQuantityGreaterThan(@RequestParam Integer quantity) {
        return productService.getProductsByQuantityGreaterThan(quantity);
    }

    // METODI PARLANTI

    @GetMapping("/near-expiration")
    public List<Product> getProductsNearExpiration(@RequestParam Integer days) {
        return productService.getProductsNearExpiration(days);
    }

    @GetMapping("/consumed")
    public List<Product> getConsumedProducts() {
        return productService.getConsumedProducts();
    }

    @GetMapping("/available")
    public List<Product> getAvailableProducts() {
        return productService.getAvailableProducts();
    }

    // STATISTICHE

    @GetMapping("/statistics/average-quantity-per-category")
    public Map<Category, Double> getAverageQuantityPerCategory() {
        return productService.getAverageQuantityPerCategory();
    }

    @GetMapping("/statistics/consumed-and-expired-percentage")
    public Map<String, Double> getConsumedAndExpiredPercentage() {
        return productService.getConsumedAndExpiredPercentage();
    }

    @GetMapping("/statistics/most-consumed-category")
    public Category getMostConsumedCategory() {
        return productService.getMostConsumedCategory();
    }

    @GetMapping("/statistics/total-per-category")
    public Map<Category, Long> getTotalProductsPerCategory() {
        return productService.getTotalProductsPerCategory();
    }

    @PostMapping("/{id}/consume")
    public Product consumeProduct(@PathVariable Long id, @RequestParam int amount) {
        return productService.consumeProduct(id, amount);
    }

}
