package priceobservatory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceobservatory.dto.ProductDTO;
import priceobservatory.dto.ShopDTO;
import priceobservatory.exception.ShopNotFoundException;
import priceobservatory.json.ShopJson;
import priceobservatory.model.Product;
import priceobservatory.model.Shop;
import priceobservatory.repository.ShopRepository;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ShopService {
    private final ValidationService validationService;
    private final ShopRepository shopRepository;

    ShopService(
            @Autowired ValidationService validationService,
            @Autowired ShopRepository shopRepository
    ) {
        this.validationService = validationService;
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

    public Shop save(Shop shop) {
        return shopRepository.save(shop);
    }

    public List<Shop> findAll() {
        return shopRepository.findAll();
    }

    public void deleteById(Integer id) {
        shopRepository.deleteById(id);
    }

    public Optional<Shop> findById(Integer shopId) {
        return shopRepository.findById(shopId);
    }

    public String getShops(Integer start, Integer count, String status, String sort, String format) throws JsonProcessingException {
        validationService.validateGetParameters(start, count, status, sort, format);

        List<Shop> shops;
        List<ShopDTO> shopDTOs = new ArrayList<ShopDTO>();

        String[] splitSort = sort.split("\\|");
        String sortBy = splitSort[0];
        String sortOrder = splitSort[1];

        if (status.equals("ACTIVE")) {
            shops = shopRepository.findByWithdrawnFalse();
        } else if (status.equals("WITHDRAWN")) {
            shops = shopRepository.findByWithdrawnTrue();
        } else {
            shops = shopRepository.findAll();
        }

        if (sortBy.equals("name")) {
            if (sortOrder.equals("ASC")) {
                shops.sort(Comparator.comparing(Shop::getName));
            } else {
                shops.sort(Comparator.comparing(Shop::getName).reversed());
            }
        } else {
            if (sortOrder.equals("ASC")) {
                shops.sort(Comparator.comparing(Shop::getId));
            } else {
                shops.sort(Comparator.comparing(Shop::getId).reversed());
            }
        }

        for (int i = start; i <= start+count-1 && i < shops.size(); i++) {
            shopDTOs.add(shops.get(i)._convertToShopDTO());
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(
                new ShopJson(start, count, Long.valueOf(shopDTOs.size()), shopDTOs)
        );

        return json;
    }

    public String getShops(String name, Integer start, Integer count, String status, String format) throws JsonProcessingException, UnsupportedEncodingException {
        validationService.validateGetParameters(start, count, status, format);

        name = URLDecoder.decode(name, StandardCharsets.UTF_8.displayName());

        List<Shop> shops;
        if (status.equals("ACTIVE")) {
            shops = findByWithdrawnFalseAndNameContaining(name);
        } else if (status.equals("WITHDRAWN")) {
            shops = findByWithdrawnTrueAndNameContaining(name);
        } else {
            shops= findByNameContaining(name);
        }

        List<ShopDTO> shopDTOs = new ArrayList<ShopDTO>();

        shops.sort(Comparator.comparing(Shop::getName));

        for (int i = start; i <= start+count-1 && i < shops.size(); i++) {
            shopDTOs.add(shops.get(i)._convertToShopDTO());
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(
                new ShopJson(start, count, Long.valueOf(shopDTOs.size()), shopDTOs)
        );

        return json;
    }

    public String getShop(Integer id, String format) throws JsonProcessingException {
        validationService.validateGetParameters(format);

        ShopDTO shop = findById(id)
                .orElseThrow(() -> new ShopNotFoundException(id))
                ._convertToShopDTO();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(shop);

        return json;
    }

    public String postShop(String token, ShopDTO newShop, String format) throws JsonProcessingException {
        validationService.validateToken(token);
        validationService.validatePostParameters(newShop, format);

        ShopDTO shop = save(newShop._convertToShop())._convertToShopDTO();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(shop);

        return json;
    }

    public String putShop(String token, ShopDTO newShop, Integer id, String format) throws JsonProcessingException {
        validationService.validateToken(token);
        validationService.validatePutParameters(newShop, format);

        ShopDTO shop = shopRepository.findById(id).map(s -> {
            s.setName(newShop.getName());
            s.setAddress(newShop.getAddress());
            s.setLng(newShop.getLng());
            s.setLat(newShop.getLat());
            s.setTagsFromString(newShop.getTags());
            s.setWithdrawn(newShop.getWithdrawn());

            return shopRepository.save(s)._convertToShopDTO();
        }).orElseGet(() -> shopRepository.save(newShop._convertToShop())._convertToShopDTO());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(shop);

        return json;
    }

    public String patchShop(String token, ShopDTO newShop, Integer id, String format) throws JsonProcessingException {
        validationService.validateToken(token);
        validationService.validatePatchParameters(format);

        ShopDTO shop = findById(id).map(s -> {
            if (!s.getName().equals(newShop.getName()) && newShop.getName() != null) {
                s.setName(newShop.getName());
            }

            if (!s.getAddress().equals(newShop.getAddress()) && newShop.getAddress() != null) {
                s.setAddress(newShop.getAddress());
            }

            if (!s.getLng().equals(newShop.getLng()) && newShop.getLng() != null) {
                s.setLng(newShop.getLng());
            }

            if (!s.getLat().equals(newShop.getLat()) && newShop.getLat() != null) {
                s.setLat(newShop.getLat());
            }

            if (newShop.getTags() != null) {
                s.setTagsFromString(newShop.getTags());
            }

            if (s.getWithdrawn() != newShop.getWithdrawn() && newShop.getWithdrawn() != null) {
                s.setWithdrawn(newShop.getWithdrawn());
            }

            return shopRepository.save(s)._convertToShopDTO();
        }).orElseThrow(() -> new ShopNotFoundException(id));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(shop);

        return json;
    }

    public String deleteShop(String token, Integer id, String format) {
        String role = validationService.determineRole(token);
        validationService.validateDeleteParameters(format);

        if (role.equals("admin")) {
            try {
                deleteById(id);
            } catch (Exception e) {
                throw new ShopNotFoundException(id);
            }
        } else {
            try {
                Shop shop = findById(id).get();
                shop.setWithdrawn(true);
                save(shop);
            } catch (Exception e) {
                throw new ShopNotFoundException(id);
            }
        }
        return "{\"message\": \"OK\" }";
    }
}
