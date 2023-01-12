package priceobservatory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceobservatory.model.Product;
import priceobservatory.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findByIdIn(List<String> productIds) {
        return productRepository.findByIdIn(productIds);
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
}
