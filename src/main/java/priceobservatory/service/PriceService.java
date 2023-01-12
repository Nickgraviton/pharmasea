package priceobservatory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceobservatory.model.Price;
import priceobservatory.repository.PriceRepository;

import java.util.Date;
import java.util.List;

@Service
public class PriceService {
    @Autowired
    private PriceRepository priceRepository;

    PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Price save(Price price) {
        return priceRepository.save(price);
    }

    public List<Price> findAll() {
        return priceRepository.findAll();
    }

    public List<Price> findByShopIdInAndProductIdInAndDateBetween(List<Integer> shopIds, List<Integer> productIds, Date dateFrom, Date dateTo) {
        return priceRepository.findByShopIdInAndProductIdInAndDateBetween(shopIds, productIds, dateFrom, dateTo);
    }
    public List<Price> findByShopIdInAndDateBetween(List<Integer> shopIds, Date dateFrom, Date dateTo) {
        return priceRepository.findByShopIdInAndDateBetween(shopIds, dateFrom, dateTo);
    }
    public List<Price> findByProductIdInAndDateBetween(List<Integer> productIds, Date dateFrom, Date dateTo) {
        return priceRepository.findByProductIdInAndDateBetween(productIds, dateFrom, dateTo);
    }
    public List<Price> findByDateBetween(Date dateFrom, Date dateTo) {
        return priceRepository.findByDateBetween(dateFrom, dateTo);
    }
}
