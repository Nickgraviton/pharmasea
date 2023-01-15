package priceobservatory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priceobservatory.dto.ProductDTO;
import priceobservatory.service.ProductService;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping(path = "/observatory/api/products")
public class ProductController {
    @Autowired
    ProductService productService;

    // Returns JSON response with all products
    @GetMapping(produces = "application/json")
    String getProducts(
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "20") Integer count,
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(defaultValue = "id|DESC") String sort,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return productService.getProducts(start, count, status, sort, format);
    }

    // Returns JSON response with product that has the given id
    @GetMapping(path = "/{id}", produces = "application/json")
    String getProduct(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return productService.getProduct(id, format);
    }

    // Returns JSON response with products like name
    @GetMapping(path = "/name/{name}", produces = "application/json")
    String getProducts(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "20") Integer count,
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException, UnsupportedEncodingException {
        return productService.getProducts(name, start, count, status, format);
    }

    // Creates new product
    @PostMapping(produces = "application/json")
    String postProduct(
            @RequestHeader("X-OBSERVATORY-AUTH") String token,
            @ModelAttribute("product") ProductDTO newProduct,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return productService.postProduct(token, newProduct, format);
    }

    // Replaces existing product that has the given id
    @PutMapping(path = "/{id}", produces = "application/json")
    String putProduct(
            @RequestHeader("X-OBSERVATORY-AUTH") String token,
            @ModelAttribute("product") ProductDTO newProduct,
            @PathVariable Integer id,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return productService.putProduct(token, newProduct, id, format);
    }

    // Patches existing product based on input
    @PatchMapping(path = "/{id}", produces = "application/json")
    String patchProduct(
            @RequestHeader("X-OBSERVATORY-AUTH") String token,
            @ModelAttribute("product") ProductDTO newProduct,
            @PathVariable Integer id,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return productService.patchProduct(token, newProduct, id, format);
    }

    // Deletes product with given id
    @DeleteMapping(path = "/{id}", produces = "application/json")
    String deleteProduct(
            @RequestHeader("X-OBSERVATORY-AUTH") String token,
            @PathVariable Integer id,
            @RequestParam(defaultValue = "json") String format
    ) {
        return productService.deleteProduct(token, id, format);
    }
}
