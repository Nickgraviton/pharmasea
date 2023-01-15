package priceobservatory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priceobservatory.dto.PriceDTO;
import priceobservatory.service.PriceService;

import java.util.*;

@RestController
@RequestMapping(path = "/observatory/api/prices")
public class PriceController {
    @Autowired
    private PriceService priceService;

    // Returns JSON response with all prices
    @GetMapping(produces = "application/json")
    String getPrices(
            @RequestParam(required = false) List<Integer> shops,
            @RequestParam(required = false) List<Integer> products,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) Integer geoDist,
            @RequestParam(required = false) Double geoLng,
            @RequestParam(required = false) Double geoLat,
            @RequestParam(name = "dateFrom", required = false) String dateFromString,
            @RequestParam(name = "dateTo", required = false) String dateToString,
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "20") Integer count,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return priceService.getPrices(
                shops,
                products,
                tags,
                sort,
                geoDist,
                geoLng,
                geoLat,
                dateFromString,
                dateToString,
                start,
                count,
                format
        );
    }

    // Creates new price
    @PostMapping(produces = "application/json")
    String postPrice(
            @RequestHeader("X-OBSERVATORY-AUTH") String token,
            @ModelAttribute("price") PriceDTO newPrice,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return priceService.postPrice(token, newPrice, format);
    }
}
