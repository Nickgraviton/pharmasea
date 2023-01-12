package priceobservatory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceobservatory.model.Shop;
import priceobservatory.repository.ShopRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {
    @Autowired
    private ShopRepository shopRepository;

    ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public List<Shop> findByIdIn(List<String> shopIds) {
        return shopRepository.findByIdIn(shopIds);
    }

    public List<Shop> findByNameContaining(String name) {
        return shopRepository.findByNameContaining(name);
    }

    public List<Shop> findByWithdrawnFalse() {
        return shopRepository.findByWithdrawnFalse();
    }

    public List<Shop> findByWithdrawnFalseAndNameContaining(String name) {
        return shopRepository.findByWithdrawnFalseAndNameContaining(name);
    }

    public List<Shop> findByWithdrawnTrue() {
        return shopRepository.findByWithdrawnTrue();
    }

    public List<Shop> findByWithdrawnTrueAndNameContaining(String name) {
        return shopRepository.findByWithdrawnTrueAndNameContaining(name);
    }

    public List<Shop> findAll() {
        return shopRepository.findAll();
    }

    public Optional<Shop> findById(Integer shopId) {
        return shopRepository.findById(shopId);
    }
}
