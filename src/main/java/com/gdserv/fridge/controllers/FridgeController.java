package com.gdserv.fridge.controllers;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gdserv.fridge.Category;
import com.gdserv.fridge.models.Product;
import com.gdserv.fridge.services.ProductService;

@Controller
@RequestMapping("/fridge")
public class FridgeController {

    @Autowired
    ProductService productService;

    LocalDate today = LocalDate.now();

    @GetMapping
    public String fridgeHomepage(Model model) {
        model.addAttribute("title", "Hompage Fridge");
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("prodGood", productService.getProductsByExpirationAfter(today));
        model.addAttribute("prodNoGood", productService.getExpiredProductsBefore(today));
        return "fridgeHomepage";
    }

    @GetMapping("/{category}")
    public String categoryPage(@PathVariable("category") Category category, Model model) {
        model.addAttribute("title", category.name());
        model.addAttribute("productsCategory", productService.getProductsByCategory(category));
        return "pagCategory";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", Category.values());
        return "createProduct";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product) {
        // Calcolo automatico della data di scadenza (se vuoi)
        if (product.getDaysExpiration() != null && product.getDaysExpiration() > 0) {
            product.setExpirationDate(java.time.LocalDate.now().plusDays(product.getDaysExpiration()));
        }

        productService.save(product);
        return "redirect:/fridge";
    }

    @GetMapping("/stats")
    public String showStatistics(Model model) {
        Map<Category, Double> mediaCat = productService.getAverageQuantityPerCategory();
        model.addAttribute("mediaCat", mediaCat);

        Map<String, Double> percConsScad = productService.getConsumedAndExpiredPercentage();
        model.addAttribute("percConsScad", percConsScad);

        Category catMaggiore = productService.getMostConsumedCategory();
        model.addAttribute("catMaggiore", catMaggiore);

        Map<Category, Long> prodCat = productService.getTotalProductsPerCategory();
        model.addAttribute("prodCat", prodCat);

        return "stat";
    }

}
