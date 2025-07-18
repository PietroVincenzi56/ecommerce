package com.example.ecommerce.controllers;

import com.example.ecommerce.entities.Product;
import com.example.ecommerce.exceptions.BarCodeAlreadyExistException;
import com.example.ecommerce.other.Message;
import com.example.ecommerce.repositories.ProductRepository;
import com.example.ecommerce.services.ProductService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity createProduct(@RequestBody Product product) {  //@Valid non va e non so perchè
        try {
            productService.addProduct(product);
        } catch (BarCodeAlreadyExistException e) {
            return new ResponseEntity<>(new Message("barcode inesistente"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message("Prodotto aggiunto"), HttpStatus.OK);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/search/by_name")
    public ResponseEntity getProductByName(@RequestParam(required = false) String name) {
        List<Product> ret = productService.getProductsByName(name);
        if(ret.isEmpty())
            return new ResponseEntity<>(new Message("Prodotto non trovato"), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/search/by_prize")
    public ResponseEntity getProductByPrize(@RequestParam(required = false) Float prize) {
        List<Product> ret = productService.getProductsByPrice(prize);
        if(ret.isEmpty())
            return new ResponseEntity<>(new Message("Prodotto non trovato"), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/search/by_code")
    public ResponseEntity getProductByCode(@RequestParam(required = false) String code) {
        List<Product> ret = productService.getProductsByCode(code);
        if(ret.isEmpty())
            return new ResponseEntity<>(new Message("Prodotto non trovato"), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/search/by_id")
    public ResponseEntity getProductById(@RequestParam(required = false) int id) {
        Optional<Product> ret = productService.getProductsById(id);
        if(ret.isEmpty())
            return new ResponseEntity<>(new Message("Prodotto non trovato"), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }


    @GetMapping("/paged")
    public ResponseEntity getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Product> ret = productService.getAllProducts(pageNumber, pageSize, sortBy);
        if ( ret.isEmpty() ) {
            return new ResponseEntity<>(new Message("Prodotto non trovato"), HttpStatus.OK);
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        if (!productService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Prodotto con ID " + id + " non trovato.");
        }

        productService.deleteProduct(id);
        return ResponseEntity.ok("Prodotto eliminato con successo.");
    }

    @PreAuthorize("hasRole('Admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody Product updatedProduct) {
        return ResponseEntity.ok(productService.updateProduct(id, updatedProduct));
    }

}
