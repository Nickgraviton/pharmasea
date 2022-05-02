package priceobservatory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import priceobservatory.model.Shop;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Integer> {
	List<Shop>  findByIdIn(List<String> shopIds);
	List<Shop>  findByNameContaining(String name);
	List<Shop>  findByWithdrawnFalse();
	List<Shop>  findByWithdrawnFalseAndNameContaining(String name);
	List<Shop>  findByWithdrawnTrue();
	List<Shop>  findByWithdrawnTrueAndNameContaining(String name);
	List<Shop>  findAll();
}
