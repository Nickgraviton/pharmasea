package priceobservatory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceobservatory.dto.PriceDTO;
import priceobservatory.dto.PriceFinal;
import priceobservatory.exception.BadRequestException;
import priceobservatory.json.PriceJson;
import priceobservatory.model.*;
import priceobservatory.repository.PriceRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class PriceService {
    private final ValidationService validationService;
    private final ProductService productService;
    private final ShopService shopService;
    private final PriceRepository priceRepository;

    PriceService(
            @Autowired ValidationService validationService,
            @Autowired ProductService productService,
            @Autowired ShopService shopService,
            @Autowired PriceRepository priceRepository
    ) {
        this.validationService = validationService;
        this.productService = productService;
        this.shopService = shopService;
        this.priceRepository = priceRepository;
    }

    public Price save(Price price) {
        return priceRepository.save(price);
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

    public String getPrices(
            List<Integer> shops,
            List<Integer> products,
            List<String> tags,
            List<String> sort,
            Integer geoDist,
            Double geoLng,
            Double geoLat,
            String dateFromString,
            String dateToString,
            Integer start,
            Integer count,
            String format
    ) throws JsonProcessingException {
        if (sort == null)
            sort = List.of("price|ASC");

        Date dateFrom, dateTo;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateFrom = formatter.parse(dateFromString); // (pattern = "yyyy-MM-dd")
            dateTo = formatter.parse(dateToString);
        } catch (ParseException p) {
            throw new BadRequestException("Error: Wrong date format");
        }

        validationService.validateGetParameters(start, count, format, geoDist, geoLng, geoLat, dateFrom, dateTo, sort);

        Set<String> alreadySeenSort = new HashSet<>();
        boolean priceSort = false, dateSort = false, distSort = false;
        Integer priceSortOrder = 0, dateSortOrder = 0, distSortOrder = 0;

        for (String s : sort) {
            String[] splitSort = s.split("\\|");
            String sortBy = splitSort[0];
            String sortOrder = splitSort[1];

            if (alreadySeenSort.contains(sortBy))
                throw new BadRequestException("Sorting category" + sortBy + "already specified");

            switch (sortBy) {
                case "price":
                    priceSort = true;
                    if (sortOrder.equals("ASC"))
                        priceSortOrder = 1;
                    else
                        priceSortOrder = -1;
                    break;
                case "geoDist":
                    distSort = true;
                    if (sortOrder.equals("ASC"))
                        distSortOrder = 1;
                    else
                        distSortOrder = -1;
                    break;
                case "date":
                    dateSort = true;
                    if (sortOrder.equals("ASC"))
                        dateSortOrder = 1;
                    else
                        dateSortOrder = -1;
            }
            alreadySeenSort.add(sortBy);
        }

        if (dateFrom == null && dateTo == null) {
            Date today = new Date();
            dateFrom = today;
            dateTo = today;
        }

        List<Price> prices;
        if (shops != null && products != null)
            prices = findByShopIdInAndProductIdInAndDateBetween(shops, products, dateFrom, dateTo);
        else if (shops != null)
            prices = findByShopIdInAndDateBetween(shops, dateFrom, dateTo);
        else if (products != null)
            prices = findByProductIdInAndDateBetween(products, dateFrom, dateTo);
        else
            prices = findByDateBetween(dateFrom, dateTo);

        Set<String> inputTags;
        List<PriceFinal> finalPrices = new ArrayList<>();
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

                finalPrice.setShopTags(new ArrayList<>(sTags));
                finalPrice.setProductTags(new ArrayList<>(pTags));

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
    }

    public String postPrice(String token, PriceDTO newPrice, String format) throws JsonProcessingException {
        validationService.validateToken(token);
        validationService.validatePostParameters(newPrice, format);

        Product productTemp = productService.findById(newPrice.getProductId()).get();
        List<ProductTag> productTagTemp = productTemp.getTags();
        List<String> pTags = productTagTemp.stream().map(ProductTag::getName).collect(toList());

        Shop shopTemp = shopService.findById(newPrice.getShopId()).get();
        List<ShopTag> shopTagTemp = shopTemp.getTags();
        List<String> sTags = shopTagTemp.stream().map(ShopTag::getName).collect(toList());

        List<PriceFinal> finalPrices = new ArrayList<>();
        String from = newPrice.getDateFrom();
        String to = addOneDay(newPrice.getDateTo());
        PriceDTO p;
        do {
            p = save(newPrice._convertToPrice())._convertToPriceDTO();

            PriceFinal finalPrice = new PriceFinal();
            finalPrice.setPrice(p.getPrice());
            finalPrice.setProductId(p.getProductId());
            finalPrice.setShopId(p.getShopId());
            finalPrice.setDateFromString(p.getDateFrom());
            finalPrice.setProductName(productTemp.getName());
            finalPrice.setShopName(shopTemp.getName());
            finalPrice.setShopAddress(shopTemp.getAddress());
            finalPrice.setShopTags(new ArrayList<>(sTags));
            finalPrice.setProductTags(new ArrayList<>(pTags));
            finalPrice.setShopDist(0);

            finalPrices.add(finalPrice);

            from = addOneDay(from);
            newPrice.setDateFrom(from);
        } while (!from.equals(to));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(new PriceJson(0, finalPrices.size(), Long.valueOf(finalPrices.size()), finalPrices));
        return json;
    }

    private String addOneDay(String date) {
        return LocalDate.parse(date).plusDays(1).toString();
    }
}
