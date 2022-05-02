package priceobservatory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import priceobservatory.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	List<Product>  findByIdIn(List<String> productIds);
	List<Product>  findByNameContaining(String name);
	List<Product>  findByWithdrawnFalse();
	List<Product>  findByWithdrawnFalseAndNameContaining(String name);
	List<Product>  findByWithdrawnTrue();
	List<Product>  findByWithdrawnTrueAndNameContaining(String name);
	List<Product>  findAll();
}
