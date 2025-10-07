package com.gdserv.fridge.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gdserv.fridge.Category;
import com.gdserv.fridge.models.Product;
import com.gdserv.fridge.repositories.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    // METODI CRUD BASE CREA/AGGIORNA, TROVA, ELIMINA
    @Override
    public Product save(Product product) {
        if (product.getCategory() != null) {

            return productRepository.save(product);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public void delete(Long id) {

        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found, id null"));
    }

    @Override
    public List<Product> getProductsByCategory(Category category) {
        if (category != null) {

            return productRepository.findByCategory(category);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        }
    }

    @Override
    public List<Product> getProductsByName(String name) {
        if (name == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {

            return productRepository.findByName(name);
        }
    }

    // METODI REPOSITORY PER QUERY PIU ARTICOLATE MA PARLANTI

    // LOGICA PER RITORNARE UNA LISTA DI PRODOTTI CHE SCADUTI ENTRO UNA CERTA DATA
    @Override
    public List<Product> getExpiredProductsBefore(LocalDate date) {

        return productRepository.findByExpirationDateBefore(date);
    }

    // PRODOTTI CHE NON SONO SCADUTI ENTRO UNA CERTA DATA
    @Override
    public List<Product> getProductsByExpirationAfter(LocalDate date) {

        return productRepository.findByExpirationDateAfter(date);
    }

    // PRODOTTI CHE SCADONO IN UN INTERVALLLO DI DATE
    @Override
    public List<Product> getProductsByExpirationBetween(LocalDate startDate, LocalDate endDate) {

        return productRepository.findByExpirationDateBetween(startDate, endDate);
    }

    // PRODOTTI CON QUANTITA UGUALE A UN TOT
    @Override
    public List<Product> getProductsByQuantityEquals(Integer quantity) {
        return productRepository.findByQuantityEquals(quantity);
    }

    // PRODOTTI CON QUANTITA MAGGIORI DI UN TOT
    @Override
    public List<Product> getProductsByQuantityGreaterThan(Integer quantity) {
        return productRepository.findByQuantityGreaterThan(quantity);
    }

    /*
     * METODI DIRETTI PER I PRODOTTI CONSUMATI, DISPONIBILI E QUELLI VICINI ALLA
     * SCADENZA CHE RICHIAMANO QUELLI DELLE QUERY DIRETTE PARLANTI
     */

    /*
     * USANDO IL METODO CHE RITORNA I PRODOTTI CON SCADENZA TRA DUE DATE, DEFEINISCO
     * LA DATA DI OGGI E LA CONFRONTO CON UNA DATA DEFINITA DA UN INTEGER CHE PASSO
     * PER CUI MI RITORNANO I PORODTTI CHE SCADONO ENTRO UN CERTO TOT DI GIORNI,
     * RENDENDO PIU VELOCE IL METODO PERCHE COSI NON DEVO SEMPRE PASSARE LA DATA DI
     * OGGI MA BASTA CHE DEFINISCO I GIORNI FRA QUANTO VOGLIO CHE SCADANO
     */
    @Override
    public List<Product> getProductsNearExpiration(Integer days) {
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(days);
        return productRepository.findByExpirationDateBetween(today, limitDate);
    }

    /*
     * METODO CHE DEFINISCE DIRETTAMENTE I PRODOTTI CONSUMATI IMPOSTANDO IL METODO
     * DELLA QUANTITA A 0 SENZA DOVER PASSAR NULLA
     */
    @Override
    public List<Product> getConsumedProducts() {
        return productRepository.findByQuantityEquals(0);
    }

    /*
     * STESSO LAVORO CON I PRODOTTI ANCORA DISPONIBILI CHE HANNO PERCIO QUANTITA
     * MAGGIORI DI 0
     */
    @Override
    public List<Product> getAvailableProducts() {
        return productRepository.findByQuantityGreaterThan(0);
    }

    // STATISTICHE

    // QUANTITA MEDIA DI PRODOTTI PER OGNI CATEGORIA
    @Override
    public Map<Category, Double> getAverageQuantityPerCategory() {
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.averagingInt(Product::getQuantity)));
        /*
         * QUI RICEVO UNA LISTA CON TUTTI I PRODOTTI, LA STREAMMO PER AVERE DATI
         * MANUTENIBILI, E APPLICO IL COLLECT RAGGUPPANDOLI IN BASE ALLE CATEGORIE E PER
         * OGNUNA CON AVERAGEINT CALCOLO IN AUTOMATICO LA MEDIA DELLE QUANTITA DEI
         * PRODOTTI DI QUELLA CATEGORIA
         */
    }

    // TORNO UNA MAP CON CHIAVI CONSUMATI E SCADUTI
    @Override
    public Map<String, Double> getConsumedAndExpiredPercentage() {
        List<Product> allProducts = productRepository.findAll();
        int total = allProducts.size();

        if (total == 0) {
            return Map.of(
                    "PRODOTTI CONSUMATI", 0.0,
                    "PRODOTTI SCADUTI", 0.0);
        }

        // TROVO IL NUMERO DEI CONSUMATI CON LO STREAM(POTREI ANCHE RICHIAMARE IL METODO
        // DEI PRODOTTI A QUANTITA 0 E FARE IL SIZE)
        long consumedCount = allProducts.stream()
                .filter(p -> p.getQuantity() == 0)
                .count();

        // TROVO IL NUMERO DEGLI SCADUTI CON LO STREAM
        long expiredCount = allProducts.stream()
                .filter(p -> p.getExpirationDate().isBefore(LocalDate.now()))
                .count();

        // metodo brutale
        // LocalDate today = LocalDate.now();
        // long expiredCountBrutal = 0;
        // for (var prod : allProducts) {
        // if (prod.getExpirationDate().isBefore(today)) {
        // expiredCountBrutal++;
        // }
        // }

        double consumedPercentage = (consumedCount * 100.0) / total;
        double expiredPercentage = (expiredCount * 100.0) / total;

        return Map.of(
                "PRODOTTI CONSUMATI", consumedPercentage,
                "PRODOTTI SCADUTI", expiredPercentage);

    }

    // CATEGORIA DI PRODOTTI CONSUMATI PIU UTULIZZATA
    @Override
    public Category getMostConsumedCategory() {
        // creo una lista dei prodotti consumati (quantita=0)
        List<Product> consumedProduct = productRepository.findByQuantityEquals(0);

        // creo una map di ogni categoria con quanti prodotti sono consumati,streammando
        // i consumati e raccogliendoli in una collect
        // dove conti i prodotti
        Map<Category, Long> countByCategory = consumedProduct.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        // ritorno la categoria con il long maggiore facendo prima la conversione da map
        // in un set con insime di entry, fa i solito stream per processarli, cerca con
        // max il valore massimo e il map estrae solo la chiave ossia una categoria
        return countByCategory.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
                .orElse(null);

        // PRENDO TUTTI I PRODOTTI CONSUMATI, LI CONTROPER OGNI CATEGORIA E RESTITUISCO
        // QUELLA MAGGIORE.
    }

    // ritorno una map con le categoria e tutti i prodotti per ognuna
    @Override
    public Map<Category, Long> getTotalProductsPerCategory() {
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.counting()));
        // STREAMMO TUTTI I PRODOTTI PER APPLICARGLI IL COLLECT E LI RAGGRUPPO PER
        // CATEGORIA E CONTO QUANTI PRODOTTI CI SONO
    }

    // CONSUMO
    @Override
    public Product consumeProduct(Long productId, int amountToConsume) {
        if (amountToConsume <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount to consume must be positive");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getQuantity() < amountToConsume) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough quantity to consume");
        }

        // Riduce la quantità del prodotto
        product.setQuantity(product.getQuantity() - amountToConsume);

        // Salva il prodotto aggiornato
        return productRepository.save(product);
    }

}
