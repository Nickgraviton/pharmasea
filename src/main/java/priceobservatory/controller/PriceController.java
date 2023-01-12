package priceobservatory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priceobservatory.dto.PriceDTO;
import priceobservatory.dto.PriceFinal;
import priceobservatory.exception.BadRequestException;
import priceobservatory.exception.UnauthorizedException;
import priceobservatory.json.PriceJson;
import priceobservatory.model.*;
import priceobservatory.service.ProductService;
import priceobservatory.service.ShopService;
import priceobservatory.service.PriceService;
import priceobservatory.service.TokenService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping(path = "/observatory/api/prices")
public class PriceController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PriceService priceService;

    // returns JSON with all prices
    @GetMapping(produces = "application/json")
    String allPrices(@RequestParam Map<String, String> params,
                     @RequestParam(value = "shops", required = false) Integer[] shopsList,
                     @RequestParam(value = "products", required = false) Integer[] productsList,
                     @RequestParam(value = "tags", required = false) String[] tagsList,
                     @RequestParam(value = "sort", required = false) String[] sortList) throws JsonProcessingException {

        Integer start, count, geoDist;
        Double geoLng, geoLat;
        Date dateFrom, dateTo;
        List<Integer> shops, products;
        List<String> sort, tags;
        String format;
        boolean priceSort = false, dateSort = false, distSort = false;
        Integer priceSortOrder = 0, dateSortOrder = 0, distSortOrder = 0;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        start    = (params.get("start")   == null) ? 0      : Integer.valueOf(params.get("start"));
        count    = (params.get("count")   == null) ? 20     : Integer.valueOf(params.get("count"));
        format   = (params.get("format")  == null) ? "json" : params.get("format");
        geoDist  = (params.get("geoDist") == null) ? null   : Integer.valueOf(params.get("geoDist"));
        geoLng   = (params.get("geoLng")  == null) ? null   : Double.valueOf(params.get("geoLng"));
        geoLat   = (params.get("geoLat")  == null) ? null   : Double.valueOf(params.get("geoLat"));
        shops    = (shopsList             == null) ? null   : Arrays.asList(shopsList);
        products = (productsList          == null) ? null   : Arrays.asList(productsList);
        tags     = (tagsList              == null) ? null   : Arrays.asList(tagsList);

        if (sortList == null) {
            sort = new ArrayList<String>();
            sort.add("price|ASC");
        } else {
            sort = Arrays.asList(sortList);
        }
        try {
            dateFrom = formatter.parse(params.get("dateFrom")); // (pattern = "yyyy-MM-dd")
            dateTo = formatter.parse(params.get("dateTo"));
        } catch (ParseException p) {
            throw new BadRequestException("Error: Wrong date format");
        }

        boolean validStart = true, validCount = true, validSort = true, validDist = false, validDate = false,
                validFormat = true;
        Set<String> alreadySeenSort = new HashSet<>();
        Set<String> inputTags;

        if (start < 0)
            validStart = false;
        if (count <= 0)
            validCount = false;
        if ((geoDist != null && geoLng != null && geoLat != null)
                || (geoDist == null && geoLng == null && geoLat == null))
            validDist = true;
        if ((dateFrom != null && dateTo != null) || (dateFrom == null && dateTo == null))
            validDate = true;
        if (sort.size() > 3)
            validSort = false;
        if (!format.equals("json"))
            validFormat = false;

        String[] acceptableSort = { "geoDist|ASC", "geoDist|DESC", "price|ASC", "price|DESC", "date|ASC", "date|DESC" };
        for (String s : sort) {
            if (!Arrays.asList(acceptableSort).contains(s))
                validSort = false;
        }

        List<PriceFinal> finalPrices = new ArrayList<PriceFinal>();

        if (validStart && validCount && validSort && validDist && validDate && validFormat) {
            for (String s : sort) {
                String[] splitSort = s.split("\\|");
                String sortBy = splitSort[0];
                String sortOrder = splitSort[1];

                if (sortBy.equals("price")) {
                    priceSort = true;
                    if (sortOrder.equals("ASC"))
                        priceSortOrder = 1;
                    else
                        priceSortOrder = -1;
                } else if (sortBy.equals("geoDist")) {
                    distSort = true;
                    if (sortOrder.equals("ASC"))
                        distSortOrder = 1;
                    else
                        distSortOrder = -1;
                } else {
                    dateSort = true;
                    if (sortOrder.equals("ASC"))
                        dateSortOrder = 1;
                    else
                        dateSortOrder = -1;
                }
                alreadySeenSort.add(sortBy);
            }

            Date fromParam, toParam, today;
            if (dateFrom == null && dateTo == null) {
                today = new Date();
                fromParam = today;
                toParam = today;
            } else {
                fromParam = dateFrom;
                toParam = dateTo;
            }

            List<Price> prices;
            if (shops != null && products != null)
                prices = priceService.findByShopIdInAndProductIdInAndDateBetween(shops, products, fromParam, toParam);
            else if (shops != null && products == null)
                prices = priceService.findByShopIdInAndDateBetween(shops, fromParam, toParam);
            else if (shops == null && products != null)
                prices = priceService.findByProductIdInAndDateBetween(products, fromParam, toParam);
            else
                prices = priceService.findByDateBetween(fromParam, toParam);

            for (Price p : prices) {
                // Find if there is an intersection between the input and the tags found
                if (tags != null)
                    inputTags = new HashSet<>(tags);
                else
                    inputTags = null;

                Product productTemp = productService.findById(p.getProductId()).get();
                List<ProductTag> productTagTemp = productTemp.getTags();
                Set<String> pTags = productTagTemp.stream().map(ProductTag::getName).collect(toSet());

                Shop shopTemp = shopService.findById(p.getShopId()).get();
                List<ShopTag> shopTagTemp = shopTemp.getTags();
                Set<String> sTags = shopTagTemp.stream().map(ShopTag::getName).collect(toSet());

                if (tags == null || (!Collections.disjoint(inputTags, pTags) || !Collections.disjoint(inputTags, sTags))) {
                    PriceFinal finalPrice = new PriceFinal();

                    finalPrice.setPrice(p.getPrice());
                    finalPrice.setProductId(p.getProductId());
                    finalPrice.setShopId(p.getShopId());
                    finalPrice.setDate(p.getDate());

                    finalPrice.setProductName(productTemp.getName());
                    finalPrice.setShopName(shopTemp.getName());
                    finalPrice.setShopAddress(shopTemp.getAddress());

                    finalPrice.setShopTags(sTags.stream().collect(toList()));
                    finalPrice.setProductTags(pTags.stream().collect(toList()));

                    if (geoDist != null) {
                        Double lat1 = shopTemp.getLat();
                        Double lng1 = shopTemp.getLng();
                        Double lat2 = geoLat;
                        Double lng2 = geoLng;

                        // Haversine
                        double dLat = Math.toRadians(lat2 - lat1);
                        double dLon = Math.toRadians(lng2 - lng1);
                        lat1 = Math.toRadians(lat1);
                        lat2 = Math.toRadians(lat2);

                        double a = Math.pow(Math.sin(dLat / 2), 2)
                                + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
                        double c = 2 * Math.asin(Math.sqrt(a));
                        Integer distance = (int) (6372.8 * c);

                        if (distance <= geoDist + 1) {
                            finalPrice.setShopDist(distance);
                            finalPrices.add(finalPrice);
                        }
                    } else {
                        finalPrices.add(finalPrice);

                    }
                }
            }

            if (priceSort) {
                if (priceSortOrder == 1) {
                    finalPrices.sort(Comparator.comparing(PriceFinal::getPrice));
                } else {
                    finalPrices.sort(Comparator.comparing(PriceFinal::getPrice).reversed());
                }
            } else if (distSort){
                if (distSortOrder == 1) {
                    finalPrices.sort(Comparator.comparing(PriceFinal::getShopDist));
                } else {
                    finalPrices.sort(Comparator.comparing(PriceFinal::getShopDist).reversed());
                }
            } else if (dateSort) {
                if (dateSortOrder == 1) {
                    finalPrices.sort(Comparator.comparing(PriceFinal::getDate));
                } else {
                    finalPrices.sort(Comparator.comparing(PriceFinal::getDate).reversed());
                }
            }
            List<PriceFinal> finalPricesToJson = new ArrayList<PriceFinal>();

            for (int i = start; i <= start+count-1 && i < finalPrices.size(); i++) {
                finalPricesToJson.add(finalPrices.get(i));
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            String json = mapper.writeValueAsString(new PriceJson(start, count, Long.valueOf(finalPrices.size()), finalPricesToJson));
            return json;
        } else {
            StringBuilder builder = new StringBuilder("Error: ");
            if (!validStart)
                builder.append("Invalid start parameter, ");
            if (!validCount)
                builder.append("Invalid count parameter, ");
            if (!validSort)
                builder.append("Invalid sort parameter, ");
            if (!validDist)
                builder.append("Invalid distance parameters, ");
            if (!validDate)
                builder.append("Invalid date parameters, ");
            if (!validFormat)
                builder.append("Invalid format, ");
            builder.setLength(builder.length() - 2);
            throw new BadRequestException(builder.toString());
        }
    }

    public String addOneDay(String date) {
        return LocalDate.parse(date).plusDays(1).toString();
    }

    // creates new shop
    @PostMapping(produces = "application/json")
    String newPrice(@RequestHeader("X-OBSERVATORY-AUTH") String token, @RequestParam Map<String, String> params)
            throws JsonProcessingException, ParseException{

        PriceDTO newPrice = new PriceDTO(Double.valueOf(params.get("price")), params.get("dateFrom"),
                params.get("dateTo"), Integer.valueOf(params.get("productId")), Integer.valueOf(params.get("shopId")));

        String format = (params.get("format") == null) ? "json" : params.get("format");
        if (token == null)
            throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
        String role = tokenService.determineRole(token);

        if (!format.equals("json"))
            throw new BadRequestException("Error: Invalid format");
        if (newPrice.anyNull())
            throw new BadRequestException("Invalid price field(s)");

        Product productTemp = productService.findById(Integer.valueOf(params.get("productId"))).get();
        List<ProductTag> productTagTemp = productTemp.getTags();
        List<String> pTags = productTagTemp.stream().map(ProductTag::getName).collect(toList());

        Shop shopTemp = shopService.findById(Integer.valueOf(params.get("shopId"))).get();
        List<ShopTag> shopTagTemp = shopTemp.getTags();
        List<String> sTags = shopTagTemp.stream().map(ShopTag::getName).collect(toList());

        List<PriceFinal> finalPrices = new ArrayList<>();
        String from = newPrice.getDateFrom();
        String to = addOneDay(newPrice.getDateTo());
        PriceDTO p;
        do{
            p = priceService.save(newPrice._convertToPrice())._convertToPriceDTO();

            PriceFinal finalPrice = new PriceFinal();
            finalPrice.setPrice(p.getPrice());
            finalPrice.setProductId(p.getProductId());
            finalPrice.setShopId(p.getShopId());
            finalPrice.setDateFromString(p.getDateFrom());
            finalPrice.setProductName(productTemp.getName());
            finalPrice.setShopName(shopTemp.getName());
            finalPrice.setShopAddress(shopTemp.getAddress());
            finalPrice.setShopTags(sTags.stream().collect(toList()));
            finalPrice.setProductTags(pTags.stream().collect(toList()));
            finalPrice.setShopDist(0);

            finalPrices.add(finalPrice);

            from = addOneDay(from);
            newPrice.setDateFrom(from);
        } while(!from.equals(to));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(new PriceJson(0, finalPrices.size(), Long.valueOf(finalPrices.size()), finalPrices));
        return json;
    }
}
