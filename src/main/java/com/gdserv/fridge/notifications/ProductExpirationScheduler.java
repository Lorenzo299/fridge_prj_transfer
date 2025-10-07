package com.gdserv.fridge.notifications;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gdserv.fridge.models.Product;
import com.gdserv.fridge.services.ProductService;

@Component
public class ProductExpirationScheduler {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductNotifier productNotifier;


    // SCHEDULE DEI PRODOTTI CHE SCADRANNO ENTRO 3 GIORNI OGNI GIORNO
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkNearExpiration() {
        int daysThreshold = 3; 
        List<Product> nearExpiring = productService.getProductsNearExpiration(daysThreshold);
        for (Product p : nearExpiring) {
            productNotifier.notifyExpiring(p);
        }
    }

    
    // SCHEDULE DEI PRODOTTI GIA SCADUTI OGNI GIORNO
    @Scheduled(cron = "0 30 8 * * ?")
    public void checkExpired() {
        LocalDate today = LocalDate.now();
        List<Product> expired = productService.getExpiredProductsBefore(today);
        for (Product p : expired) {
            productNotifier.notifyExpired(p);
        }
    }
}
