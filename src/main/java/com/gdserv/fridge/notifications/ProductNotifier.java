package com.gdserv.fridge.notifications;

import com.gdserv.fridge.models.Product;

public class ProductNotifier {

    public void notifyExpiring(Product product) {
        
        System.out.println("Il prodotto "
                + product.getName() + " scadrà in data " + product.getExpirationDate());
    }

    public void notifyExpired(Product product) {
        System.out.println("Il prodotto "
                + product.getName() + " è scaduto in data: " + product.getExpirationDate());
    }
}
