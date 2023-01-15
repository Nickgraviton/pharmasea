package priceobservatory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceobservatory.dto.ProductDTO;
import priceobservatory.exception.BadRequestException;
import priceobservatory.exception.ProductNotFoundException;
import priceobservatory.json.ProductJson;
import priceobservatory.model.Product;
import priceobservatory.repository.ProductRepository;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ProductService {
    private final ValidationService validationService;
    private final ProductRepository productRepository;

    ProductService(
            @Autowired ValidationService validationService,
            @Autowired ProductRepository productRepository
    ) {
        this.validationService = validationService;
        this.productRepository = productRepository;
    }

    public List<Product> findByNameContaining(String name) {
        return productRepository.findByNameContaining(name);
    }

    public List<Product> findByWithdrawnFalse() {
        return productRepository.findByWithdrawnFalse();
    }

    public List<Product> findByWithdrawnFalseAndNameContaining(String name) {
        return productRepository.findByWithdrawnFalseAndNameContaining(name);
    }

    public List<Product> findByWithdrawnTrue() {
        return productRepository.findByWithdrawnTrue();
    }

    public List<Product> findByWithdrawnTrueAndNameContaining(String name){
        return productRepository.findByWithdrawnTrueAndNameContaining(name);
    }

    public List<Product> findAll(){
        return productRepository.findAll();
    }

    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }

    public String getProducts(Integer start, Integer count, String status, String sort, String format) throws JsonProcessingException {
        validationService.validateGetParameters(start, count, status, sort, format);

        String[] splitSort = sort.split("\\|");
        String sortBy = splitSort[0];
        String sortOrder = splitSort[1];

        List<Product> products;
        List<ProductDTO> productDTOs = new ArrayList<>();
        if (status.equals("ACTIVE")) {
            products = findByWithdrawnFalse();
        } else if (status.equals("WITHDRAWN")) {
            products = findByWithdrawnTrue();
        } else {
            products = findAll();
        }

        if (sortBy.equals("name")) {
            if (sortOrder.equals("ASC")) {
                products.sort(Comparator.comparing(Product::getName));
            } else {
                products.sort(Comparator.comparing(Product::getName).reversed());
            }
        } else {
            if (sortOrder.equals("ASC")) {
                products.sort(Comparator.comparing(Product::getId));
            } else {
                products.sort(Comparator.comparing(Product::getId).reversed());
            }
        }

        for (int i = start; i <= start + count - 1 && i < products.size(); i++) {
            productDTOs.add(products.get(i)._convertToProductDTO());
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(
                new ProductJson(start, count, Long.valueOf(productDTOs.size()), productDTOs)
        );

        return json;
    }

    public String getProducts(String name, Integer start, Integer count, String status, String format) throws UnsupportedEncodingException, JsonProcessingException {
        validationService.validateGetParameters(start, count, status, format);

        String newName;
        newName = URLDecoder.decode(name, StandardCharsets.UTF_8.displayName());

        List<Product> products;
        if (status.equals("ACTIVE")) {
            products = findByWithdrawnFalseAndNameContaining(newName);
        } else if (status.equals("WITHDRAWN")) {
            products = findByWithdrawnTrueAndNameContaining(newName);
        } else {
            products = findByNameContaining(newName);
        }

        List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();

        products.sort(Comparator.comparing(Product::getName));

        for (int i = start; i <= start + count - 1 && i < products.size(); i++) {
            productDTOs.add(products.get(i)._convertToProductDTO());
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(
                new ProductJson(start, count, Long.valueOf(productDTOs.size()), productDTOs)
        );

        return json;
    }

    public String getProduct(Integer id, String format) throws JsonProcessingException {
        validationService.validateGetParameters(format);

        ProductDTO product = findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id))
                ._convertToProductDTO();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(product);

        return json;
    }

    public String postProduct(String token, ProductDTO newProduct, String format) throws JsonProcessingException {
        validationService.validateToken(token);
        validationService.validatePostParameters(newProduct, format);

        ProductDTO product = save(newProduct._convertToProduct())._convertToProductDTO();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(product);

        return json;
    }

    public String putProduct(String token, ProductDTO newProduct, Integer id,String format) throws JsonProcessingException {
        validationService.validateToken(token);
        validationService.validatePutParameters(newProduct, format);

        ProductDTO product = findById(id).map(p -> {
            p.setName(newProduct.getName());
            p.setDescription(newProduct.getDescription());
            p.setCategory(newProduct.getCategory());
            p.setTagsFromString(newProduct.getTags());
            p.setWithdrawn(newProduct.getWithdrawn());

            return save(p)._convertToProductDTO();
        }).orElseGet(() -> save(newProduct._convertToProduct())._convertToProductDTO());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(product);

        return json;
    }

    public String patchProduct(String token, ProductDTO newProduct, Integer id,String format) throws JsonProcessingException {
        validationService.validateToken(token);
        validationService.validatePatchParameters(format);

        ProductDTO product = findById(id).map(p -> {
            if (!p.getName().equals(newProduct.getName()) && newProduct.getName() != null) {
                p.setName(newProduct.getName());
            }

            if (!p.getDescription().equals(newProduct.getDescription()) && newProduct.getDescription() != null) {
                p.setDescription(newProduct.getDescription());
            }

            if (!p.getCategory().equals(newProduct.getCategory()) && newProduct.getCategory() != null) {
                p.setCategory(newProduct.getCategory());
            }

            if (newProduct.getTags() != null) {
                p.setTagsFromString(newProduct.getTags());
            }

            if (p.getWithdrawn() != newProduct.getWithdrawn() && newProduct.getWithdrawn() != null) {
                p.setWithdrawn(newProduct.getWithdrawn());
            }

            return save(p)._convertToProductDTO();
        }).orElseThrow(() -> new ProductNotFoundException(id));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(product);

        return json;
    }

    public String deleteProduct(String token, Integer id, String format) {
        String role = validationService.determineRole(token);
        validationService.validateDeleteParameters(format);

        if (role.equals("admin")) {
            try {
                deleteById(id);
            } catch (Exception e) {
                throw new ProductNotFoundException(id);
            }
        } else {
            try {
                Product product = findById(id).get();
                product.setWithdrawn(true);
                save(product);
            } catch (Exception e) {
                throw new ProductNotFoundException(id);
            }
        }
        return "{\"message\": \"OK\" }";
    }
}
