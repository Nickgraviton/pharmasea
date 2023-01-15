package priceobservatory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priceobservatory.dto.ShopDTO;
import priceobservatory.service.ShopService;
import priceobservatory.service.TokenService;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping(path = "/observatory/api/shops")
public class ShopController {
    @Autowired
    private ShopService shopService;

    // Returns JSON response with all shops
    @GetMapping(produces = "application/json")
    String getShops(
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "20") Integer count,
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(defaultValue = "id|DESC") String sort,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return shopService.getShops(start, count, status, sort, format);
    }

    // Returns JSON response with shops like name
    @GetMapping(path = "/name/{name}", produces = "application/json")
    String getShops(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "20") Integer count,
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(defaultValue = "json") String format
    ) throws UnsupportedEncodingException, JsonProcessingException {
        return shopService.getShops(name, start, count, status, format);
    }

    // Returns JSON response with single shop
    @GetMapping(path = "/{id}", produces = "application/json")
    String getShop(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return shopService.getShop(id, format);
    }

    // Creates new shop
    @PostMapping(produces = "application/json")
    String postShop(
            @RequestHeader("X-OBSERVATORY-AUTH") String token,
            @ModelAttribute("shop") ShopDTO newShop,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return shopService.postShop(token, newShop, format);
    }

    // Replaces existing shop that has the given id
    @PutMapping(path = "/{id}", produces = "application/json")
    String putShop(
            @RequestHeader("X-OBSERVATORY-AUTH") String token,
            @ModelAttribute("shop") ShopDTO newShop,
            @PathVariable Integer id,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return shopService.putShop(token, newShop, id, format);
    }

    // Patches existing product based on input
    @PatchMapping(path = "/{id}", produces = "application/json")
    String patchShop(
            @RequestHeader("X-OBSERVATORY-AUTH") String token,
            @ModelAttribute("shop") ShopDTO newShop,
            @PathVariable Integer id,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return shopService.patchShop(token, newShop, id, format);
    }

    // Deletes shop with given id
    @DeleteMapping(path = "/{id}", produces = "application/json")
    String deleteShop(
            @RequestHeader("X-OBSERVATORY-AUTH") String token,
            @PathVariable Integer id,
            @RequestParam(defaultValue = "json") String format
    ) {
        return shopService.deleteShop(token, id, format);
    }
}
