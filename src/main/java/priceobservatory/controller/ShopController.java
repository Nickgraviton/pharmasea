package priceobservatory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priceobservatory.dto.ShopDTO;
import priceobservatory.exception.BadRequestException;
import priceobservatory.exception.ShopNotFoundException;
import priceobservatory.exception.UnauthorizedException;
import priceobservatory.json.ShopJson;
import priceobservatory.model.Shop;
import priceobservatory.repository.ShopRepository;
import priceobservatory.service.TokenService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping(path = "/observatory/api/shops")
public class ShopController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ShopRepository shopRepository;

    // returns JSON with all shops
    @GetMapping(produces = "application/json")
    String allShops(@RequestParam Map<String, String> params) throws JsonProcessingException {

        Integer start, count;
        String status, sort, format;
        start = (params.get("start") == null) ? 0 : Integer.valueOf(params.get("start"));
        count = (params.get("count") == null) ? 20 : Integer.valueOf(params.get("count"));
        status = (params.get("status") == null) ? "ACTIVE" : params.get("status");
        sort = (params.get("sort") == null) ? "id|DESC" : params.get("sort");
        format = (params.get("format") == null) ? "json" : params.get("format");

        List<Shop> shops;
        List<ShopDTO> shopDTOs = new ArrayList<ShopDTO>();
        boolean validStart = true, validCount = true, validStatus, validSort, validFormat = true;

        if (start < 0)
            validStart = false;
        if (count <= 0)
            validCount = false;
        if (!format.equals("json"))
            validFormat = false;

        String[] acceptableStatus = { "ALL", "ACTIVE", "WITHDRAWN" };
        validStatus = Arrays.asList(acceptableStatus).contains(status);

        String[] acceptableSort = { "id|ASC", "id|DESC", "name|ASC", "name|DESC" };
        validSort = Arrays.asList(acceptableSort).contains(sort);

        if (validStart && validCount && validStatus && validSort && validFormat) {
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
            String json = mapper
                    .writeValueAsString(new ShopJson(start, count, Long.valueOf(shopDTOs.size()), shopDTOs));

            return json;
        } else {
            StringBuilder builder = new StringBuilder("Error: ");
            if (!validStart)
                builder.append("Invalid start parameter, ");
            if (!validCount)
                builder.append("Invalid count parameter, ");
            if (!validStatus)
                builder.append("Invalid status parameter, ");
            if (!validSort)
                builder.append("Invalid sort parameter, ");
            if (!validFormat)
                builder.append("Invalid format, ");
            builder.setLength(builder.length() - 2);
            throw new BadRequestException(builder.toString());
        }
    }

    // returns JSON with shops like name
    @GetMapping(path = "/name/{name}", produces = "application/json")
    String shopsLike(@PathVariable String name, @RequestParam Map<String, String> params)
            throws UnsupportedEncodingException, JsonProcessingException {

        Integer start, count;
        String format, newName, status;
        start   = (params.get("start")  == null) ? 0         : Integer.valueOf(params.get("start"));
        count   = (params.get("count")  == null) ? 20        : Integer.valueOf(params.get("count"));
        status  = (params.get("status") == null) ? "ACTIVE"  : params.get("status");
        newName = (name                 == null) ? ""        : URLDecoder.decode(name, StandardCharsets.UTF_8.displayName());
        format  = (params.get("format") == null) ? "json"    : params.get("format");

        boolean validStart = true, validCount = true, validFormat = true, validStatus;

        String[] acceptableStatus = { "ALL", "ACTIVE", "WITHDRAWN" };
        validStatus = Arrays.asList(acceptableStatus).contains(status);

        if (start < 0)
            validStart = false;
        if (count <= 0)
            validCount = false;
        if (!format.equals("json"))
            validFormat = false;

        if (validStart && validCount && validFormat && validStatus) {
            List<Shop> shops;
            if (status.equals("ACTIVE")) {
                shops = shopRepository.findByWithdrawnFalseAndNameContaining(newName);
            } else if (status.equals("WITHDRAWN")) {
                shops = shopRepository.findByWithdrawnTrueAndNameContaining(newName);
            } else {
                shops= shopRepository.findByNameContaining(newName);
            }

            List<ShopDTO> shopDTOs = new ArrayList<ShopDTO>();

            shops.sort(Comparator.comparing(Shop::getName));

            for (int i = start; i <= start+count-1 && i < shops.size(); i++) {
                shopDTOs.add(shops.get(i)._convertToShopDTO());
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            String json = mapper
                    .writeValueAsString(new ShopJson(start, count, Long.valueOf(shopDTOs.size()), shopDTOs));

            return json;
        } else {
            StringBuilder builder = new StringBuilder("Error: ");
            if (!validStart)
                builder.append("Invalid start parameter, ");
            if (!validCount)
                builder.append("Invalid count parameter, ");
            if (!validStatus)
                builder.append("Invalid status parameter, ");
            if (!validFormat)
                builder.append("Invalid format, ");
            builder.setLength(builder.length() - 2);
            throw new BadRequestException(builder.toString());
        }
    }

    // creates new shop
    @PostMapping(produces = "application/json")
    ShopDTO newShop(@ModelAttribute("shop") ShopDTO newShop, @RequestHeader("X-OBSERVATORY-AUTH") String token,
                    @RequestParam Map<String, String> params) {

        String format = (params.get("format") == null) ? "json" : params.get("format");
        if (token == null)
            throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
        String role = tokenService.determineRole(token);

        if (!format.equals("json"))
            throw new BadRequestException("Error: Invalid format");
        if (newShop.anyNull())
            throw new BadRequestException("Error: Invalid shop field(s)");

        return shopRepository.save(newShop._convertToShop())._convertToShopDTO();
    }

    // returns JSON with single shop
    @GetMapping(path = "/{id}", produces = "application/json")
    ShopDTO oneShop(@PathVariable Integer id, @RequestParam Map<String, String> params) {

        String format = (params.get("format") == null) ? "json" : params.get("format");
        if (!format.equals("json"))
            throw new BadRequestException("Error: Invalid format");

        return shopRepository.findById(id).orElseThrow(() -> new ShopNotFoundException(id))._convertToShopDTO();
    }

    // replaces shop
    @PutMapping(path = "/{id}", produces = "application/json")
    ShopDTO replaceShop(@ModelAttribute("shop") ShopDTO newShop, @PathVariable Integer id,
                        @RequestHeader("X-OBSERVATORY-AUTH") String token, @RequestParam Map<String, String> params) {

        String format = (params.get("format") == null) ? "json" : params.get("format");
        if (token == null)
            throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
        String role = tokenService.determineRole(token);

        if (!format.equals("json"))
            throw new BadRequestException("Error: Invalid format");
        if (newShop.anyNull())
            throw new BadRequestException("Error: Invalid shop field(s)");

        return shopRepository.findById(id).map(shop -> {
            shop.setName(newShop.getName());
            shop.setAddress(newShop.getAddress());
            shop.setLng(newShop.getLng());
            shop.setLat(newShop.getLat());
            shop.setTagsFromString(newShop.getTags());
            shop.setWithdrawn(newShop.getWithdrawn());

            return shopRepository.save(shop)._convertToShopDTO();
        }).orElseGet(() -> {
            return shopRepository.save(newShop._convertToShop())._convertToShopDTO();
        });
    }

    // replaces partial shop
    @PatchMapping(path = "/{id}", produces = "application/json")
    ShopDTO fixShop(@ModelAttribute("shop") ShopDTO newShop, @PathVariable Integer id,
                    @RequestHeader("X-OBSERVATORY-AUTH") String token, @RequestParam Map<String, String> params) {

        String format = (params.get("format") == null) ? "json" : params.get("format");
        if (token == null)
            throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
        String role = tokenService.determineRole(token);

        if (!format.equals("json"))
            throw new BadRequestException("Invalid format");

        return shopRepository.findById(id).map(shop -> {

            if (!shop.getName().equals(newShop.getName()) && newShop.getName() != null) {
                shop.setName(newShop.getName());
            }

            if (!shop.getAddress().equals(newShop.getAddress()) && newShop.getAddress() != null) {
                shop.setAddress(newShop.getAddress());
            }

            if (!shop.getLng().equals(newShop.getLng()) && newShop.getLng() != null) {
                shop.setLng(newShop.getLng());
            }

            if (!shop.getLat().equals(newShop.getLat()) && newShop.getLat() != null) {
                shop.setLat(newShop.getLat());
            }

            if (newShop.getTags() != null) {
                shop.setTagsFromString(newShop.getTags());
            }

            if (shop.getWithdrawn() != newShop.getWithdrawn() && newShop.getWithdrawn() != null) {
                shop.setWithdrawn(newShop.getWithdrawn());
            }

            return shopRepository.save(shop)._convertToShopDTO();
        }).orElseThrow(() -> new ShopNotFoundException(id));
    }

    // deletes shop
    @DeleteMapping(path = "/{id}", produces = "application/json")
    String deleteShop(@PathVariable Integer id, @RequestHeader("X-OBSERVATORY-AUTH") String token,
                      @RequestParam Map<String, String> params) {

        String format = (params.get("format") == null) ? "json" : params.get("format");
        if (token == null)
            throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
        String role = tokenService.determineRole(token);

        if (!format.equals("json"))
            throw new BadRequestException("Error: Invalid format");

        if (role.equals("admin")) {
            try {
                shopRepository.deleteById(id);
            } catch (Exception e) {
                throw new ShopNotFoundException(id);
            }
        } else {
            try {
                Shop shop = shopRepository.findById(id).get();
                shop.setWithdrawn(true);
                shopRepository.save(shop);
            } catch (Exception e) {
                throw new ShopNotFoundException(id);
            }
        }
        return "{\"message\": \"OK\" }";
    }
}
