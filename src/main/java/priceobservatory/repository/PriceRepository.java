package priceobservatory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import priceobservatory.model.Price;

import java.util.List;
import java.util.Date;

public interface PriceRepository extends JpaRepository<Price, Integer> {
	List<Price> findAll();
	List<Price> findByShopIdInAndProductIdInAndDateBetween(List<Integer> shopIds, List<Integer> productIds, Date dateFrom, Date dateTo);
	List<Price> findByShopIdInAndDateBetween(List<Integer> shopIds, Date dateFrom, Date dateTo);
	List<Price> findByProductIdInAndDateBetween(List<Integer> productIds, Date dateFrom, Date dateTo);
	List<Price> findByDateBetween(Date dateFrom, Date dateTo);
}
