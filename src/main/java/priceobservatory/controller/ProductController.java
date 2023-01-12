package priceobservatory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priceobservatory.dto.ProductDTO;
import priceobservatory.exception.BadRequestException;
import priceobservatory.exception.ProductNotFoundException;
import priceobservatory.exception.UnauthorizedException;
import priceobservatory.json.ProductJson;
import priceobservatory.model.Product;
import priceobservatory.service.ProductService;
import priceobservatory.service.TokenService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping(path = "/observatory/api/products")
public class ProductController {
    @Autowired
    TokenService tokenService;
    @Autowired
    ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    // returns JSON with all products
    @GetMapping(produces = "application/json")
    String allProducts(@RequestParam Map<String, String> params) throws JsonProcessingException {

        Integer start, count;
        String status, sort, format;
        start  = (params.get("start")  == null) ? 0         : Integer.valueOf(params.get("start"));
        count  = (params.get("count")  == null) ? 20        : Integer.valueOf(params.get("count"));
        status = (params.get("status") == null) ? "ACTIVE"  : params.get("status");
        sort   = (params.get("sort")   == null) ? "id|DESC" : params.get("sort");
        format = (params.get("format") == null) ? "json"    : params.get("format");

        List<Product> products;
        List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
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
                products = productService.findByWithdrawnFalse();
            } else if (status.equals("WITHDRAWN")) {
                products = productService.findByWithdrawnTrue();
            } else {
                products = productService.findAll();
            }

            if (sortBy.equals("name")) {
                if (sortOrder.equals("ASC")) {
                    products.sort(Comparator.comparing(Product::getName));
                } else {
                    products.sort(Comparator.comparing(Product::getName).reversed());
                }
            } else {
                if (sortOrder.equals("ASC")) {
                    products.sort(Comparator.comparing(Product::getId));
                } else {
                    products.sort(Comparator.comparing(Product::getId).reversed());
                }
            }

            for (int i = start; i <= start+count-1 && i < products.size(); i++) {
                productDTOs.add(products.get(i)._convertToProductDTO());
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            String json = mapper.writeValueAsString(
                    new ProductJson(start, count, Long.valueOf(productDTOs.size()), productDTOs));

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

    // returns JSON with products like name
    @GetMapping(path = "/name/{name}", produces = "application/json")
    String productsLike(@PathVariable String name, @RequestParam Map<String, String> params)
            throws JsonProcessingException, UnsupportedEncodingException {

        Integer start, count;
        String format, newName, status;
        start   = (params.get("start")  == null) ? 0         : Integer.valueOf(params.get("start"));
        count   = (params.get("count")  == null) ? 20        : Integer.valueOf(params.get("count"));
        status  = (params.get("status") == null) ? "ACTIVE"  : params.get("status");
        newName = (name                 == null) ? ""        : URLDecoder.decode(name, StandardCharsets.UTF_8.displayName());;
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
            List<Product> products;
            if (status.equals("ACTIVE")) {
                products = productService.findByWithdrawnFalseAndNameContaining(newName);
            } else if (status.equals("WITHDRAWN")) {
                products = productService.findByWithdrawnTrueAndNameContaining(newName);
            } else {
                products= productService.findByNameContaining(newName);
            }

            List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();

            products.sort(Comparator.comparing(Product::getName));

            for (int i = start; i <= start+count-1 && i < products.size(); i++) {
                productDTOs.add(products.get(i)._convertToProductDTO());
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            String json = mapper.writeValueAsString(
                    new ProductJson(start, count, Long.valueOf(productDTOs.size()), productDTOs));

            return json;
        } else {
            StringBuilder builder = new StringBuilder("Error: ");
            if (!validStart)
                builder.append("Invalid start parameter, ");
            if (!validCount)
                builder.append("Invalid count parameter, ");
            if (!validFormat)
                builder.append("Invalid format, ");
            if (!validStatus)
                builder.append("Invalid status parameter, ");
            builder.setLength(builder.length() - 2);
            throw new BadRequestException(builder.toString());
        }
    }

    // creates new product
    @PostMapping(produces = "application/json")
    ProductDTO newProduct(@RequestHeader("X-OBSERVATORY-AUTH") String token,
                          @ModelAttribute("product") ProductDTO newProduct,
                          // @RequestBody ProductDTO newProduct,
                          @RequestParam Map<String, String> params) {

        String format = (params.get("format") == null) ? "json" : params.get("format");
        if (token == null)
            throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
        String role = tokenService.determineRole(token);

        if (!format.equals("json"))
            throw new BadRequestException("Error: Invalid format");
        if (newProduct.anyNull())
            throw new BadRequestException("Error: Invalid product field(s)");

        return productService.save(newProduct._convertToProduct())._convertToProductDTO();
    }

    // returns JSON with single product
    @GetMapping(path = "/{id}", produces = "application/json")
    ProductDTO oneProduct(@PathVariable Integer id, @RequestParam Map<String, String> params) {

        String format = (params.get("format") == null) ? "json" : params.get("format");

        if (!format.equals("json"))
            throw new BadRequestException("Error: Invalid format");

        return productService.findById(id).orElseThrow(() -> new ProductNotFoundException(id))
                ._convertToProductDTO();
    }

    // replaces product
    @PutMapping(path = "/{id}", produces = "application/json")
    ProductDTO replaceProduct(@ModelAttribute("product") ProductDTO newProduct, @PathVariable Integer id,
                              @RequestHeader("X-OBSERVATORY-AUTH") String token, @RequestParam Map<String, String> params) {

        String format = (params.get("format") == null) ? "json" : params.get("format");
        if (token == null)
            throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
        String role = tokenService.determineRole(token);

        if (!format.equals("json"))
            throw new BadRequestException("Error: Invalid format");
        if (newProduct.anyNull())
            throw new BadRequestException("Error: Invalid product field(s)");

        return productService.findById(id).map(product -> {
            product.setName(newProduct.getName());
            product.setDescription(newProduct.getDescription());
            product.setCategory(newProduct.getCategory());
            product.setTagsFromString(newProduct.getTags());
            product.setWithdrawn(newProduct.getWithdrawn());

            return productService.save(product)._convertToProductDTO();
        }).orElseGet(() -> {
            return productService.save(newProduct._convertToProduct())._convertToProductDTO();
        });
    }

    // replaces partial product
    @PatchMapping(path = "/{id}", produces = "application/json")
    ProductDTO fixProduct(@ModelAttribute("product") ProductDTO newProduct, @PathVariable Integer id,
                          @RequestHeader("X-OBSERVATORY-AUTH") String token, @RequestParam Map<String, String> params) {

        String format = (params.get("format") == null) ? "json" : params.get("format");
        if (token == null)
            throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
        String role = tokenService.determineRole(token);

        if (!format.equals("json"))
            throw new BadRequestException("Error: Invalid format");

        return productService.findById(id).map(product -> {

            if (!product.getName().equals(newProduct.getName()) && newProduct.getName() != null) {
                product.setName(newProduct.getName());
            }

            if (!product.getDescription().equals(newProduct.getDescription()) && newProduct.getDescription() != null) {
                product.setDescription(newProduct.getDescription());
            }

            if (!product.getCategory().equals(newProduct.getCategory()) && newProduct.getCategory() != null) {
                product.setCategory(newProduct.getCategory());
            }

            if (newProduct.getTags() != null) {
                product.setTagsFromString(newProduct.getTags());
            }

            if (product.getWithdrawn() != newProduct.getWithdrawn() && newProduct.getWithdrawn() != null) {
                product.setWithdrawn(newProduct.getWithdrawn());
            }

            return productService.save(product)._convertToProductDTO();
        }).orElseThrow(() -> new ProductNotFoundException(id));
    }

    // deletes product
    @DeleteMapping(path = "/{id}", produces = "application/json")
    String deleteProduct(@PathVariable Integer id, @RequestHeader("X-OBSERVATORY-AUTH") String token,
                         @RequestParam Map<String, String> params) {

        String format = (params.get("format") == null) ? "json" : params.get("format");
        if (token == null)
            throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
        String role = tokenService.determineRole(token);

        if (!format.equals("json"))
            throw new BadRequestException("Error: Invalid format");

        if (role.equals("admin")) {
            try {
                productService.deleteById(id);
            } catch (Exception e) {
                throw new ProductNotFoundException(id);
            }
        } else {
            try {
                Product product = productService.findById(id).get();
                product.setWithdrawn(true);
                productService.save(product);
            } catch (Exception e) {
                throw new ProductNotFoundException(id);
            }
        }
        return "{\"message\": \"OK\" }";
    }
}
