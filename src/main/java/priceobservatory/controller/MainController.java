package priceobservatory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import priceobservatory.dto.*;
import priceobservatory.exception.*;
import priceobservatory.json.*;
import priceobservatory.model.*;
import priceobservatory.repository.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping(path = "/observatory/api")
public class MainController {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ShopRepository shopRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PriceRepository priceRepository;

	@Autowired
	private TokenRepository tokenRepository;

	MainController(ProductRepository productRepository, ShopRepository shopRepository, UserRepository userRepository,
			TokenRepository tokenRepository) {
		this.productRepository = productRepository;
		this.shopRepository = shopRepository;
		this.userRepository = userRepository;
		this.tokenRepository = tokenRepository;
	}

	String determineRole(String token) {
		Optional<Token> t = tokenRepository.findByToken(token);
		if (t.isPresent()) {
			return t.get().getRole();
		} else {
			throw new BadRequestException("Invalid token");
		}
	}

	// user login
	@PostMapping(path = "/login", produces = "application/json")
	String login(@RequestParam Map<String, String> body) {
		Optional<User> optUser = userRepository.findByUsername(body.get("username"));
		if (optUser.isPresent()) {
			if (!BCrypt.checkpw(body.get("password"), optUser.get().getPassword())) {
				throw new BadRequestException("Error: Wrong username or password");
			}
			int length = 60;
			boolean useLetters = true;
			boolean useNumbers = true;
			String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
			String hash = BCrypt.hashpw(generatedString, BCrypt.gensalt(10));
			tokenRepository.save(new Token(optUser.get().getRole(), hash));
			return "{ \"token\" : \"" + hash + "\"}";
		} else {
			throw new BadRequestException("Error: Wrong username or password");
		}
	}

	// user logout
	@PostMapping(path = "/logout", produces = "application/json")
	String message(@RequestHeader("X-OBSERVATORY-AUTH") String token) {
		Optional<Token> t = tokenRepository.findByToken(token);
		if (t.isEmpty()) {
			throw new BadRequestException("Invalid token");
		}
		tokenRepository.deleteById(t.get().getId());
		return "{\"message\": \"OK\" }";
	}

	// returns JSON with all products
	@GetMapping(path = "/products", produces = "application/json")
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
				products = productRepository.findByWithdrawnFalse();
			} else if (status.equals("WITHDRAWN")) {
				products = productRepository.findByWithdrawnTrue();
			} else {
				products= productRepository.findAll();
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
	@GetMapping(path = "/products/name/{name}", produces = "application/json")
	String productsLike(@PathVariable String name, @RequestParam Map<String, String> params)
			throws JsonProcessingException {

		Integer start, count;
		String format, newName, status;
		start   = (params.get("start")  == null) ? 0         : Integer.valueOf(params.get("start"));
		count   = (params.get("count")  == null) ? 20        : Integer.valueOf(params.get("count"));
		status  = (params.get("status") == null) ? "ACTIVE"  : params.get("status");
		newName = (name                 == null) ? ""        : name;
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
				products = productRepository.findByWithdrawnFalseAndNameContaining(newName);
			} else if (status.equals("WITHDRAWN")) {
				products = productRepository.findByWithdrawnTrueAndNameContaining(newName);
			} else {
				products= productRepository.findByNameContaining(newName);
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
	@PostMapping(path = "/products", produces = "application/json")
	ProductDTO newProduct(@RequestHeader("X-OBSERVATORY-AUTH") String token,
			@ModelAttribute("product") ProductDTO newProduct,
			// @RequestBody ProductDTO newProduct,
			@RequestParam Map<String, String> params) {

		String format = (params.get("format") == null) ? "json" : params.get("format");
		if (token == null)
			throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
		String role = determineRole(token);

		if (!format.equals("json"))
			throw new BadRequestException("Error: Invalid format");
		if (newProduct.anyNull())
			throw new BadRequestException("Error: Invalid product field(s)");

		return productRepository.save(newProduct._convertToProduct())._convertToProductDTO();
	}

	// returns JSON with single product
	@GetMapping(path = "/products/{id}", produces = "application/json")
	ProductDTO oneProduct(@PathVariable Integer id, @RequestParam Map<String, String> params) {

		String format = (params.get("format") == null) ? "json" : params.get("format");

		if (!format.equals("json"))
			throw new BadRequestException("Error: Invalid format");

		return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id))
				._convertToProductDTO();
	}

	// replaces product
	@PutMapping(path = "/products/{id}", produces = "application/json")
	ProductDTO replaceProduct(@ModelAttribute("product") ProductDTO newProduct, @PathVariable Integer id,
			@RequestHeader("X-OBSERVATORY-AUTH") String token, @RequestParam Map<String, String> params) {

		String format = (params.get("format") == null) ? "json" : params.get("format");
		if (token == null)
			throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
		String role = determineRole(token);

		if (!format.equals("json"))
			throw new BadRequestException("Error: Invalid format");
		if (newProduct.anyNull())
			throw new BadRequestException("Error: Invalid product field(s)");

		return productRepository.findById(id).map(product -> {
			product.setName(newProduct.getName());
			product.setDescription(newProduct.getDescription());
			product.setCategory(newProduct.getCategory());
			product.setTagsFromString(newProduct.getTags());
			product.setWithdrawn(newProduct.getWithdrawn());

			return productRepository.save(product)._convertToProductDTO();
		}).orElseGet(() -> {
			return productRepository.save(newProduct._convertToProduct())._convertToProductDTO();
		});
	}

	// replaces partial product
	@PatchMapping(path = "/products/{id}", produces = "application/json")
	ProductDTO fixProduct(@ModelAttribute("product") ProductDTO newProduct, @PathVariable Integer id,
			@RequestHeader("X-OBSERVATORY-AUTH") String token, @RequestParam Map<String, String> params) {

		String format = (params.get("format") == null) ? "json" : params.get("format");
		if (token == null)
			throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
		String role = determineRole(token);

		if (!format.equals("json"))
			throw new BadRequestException("Error: Invalid format");

		return productRepository.findById(id).map(product -> {

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

			return productRepository.save(product)._convertToProductDTO();
		}).orElseThrow(() -> new ProductNotFoundException(id));
	}

	// deletes product
	@DeleteMapping(path = "/products/{id}", produces = "application/json")
	String deleteProduct(@PathVariable Integer id, @RequestHeader("X-OBSERVATORY-AUTH") String token,
			@RequestParam Map<String, String> params) {

		String format = (params.get("format") == null) ? "json" : params.get("format");
		if (token == null)
			throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
		String role = determineRole(token);

		if (!format.equals("json"))
			throw new BadRequestException("Error: Invalid format");

		if (role.equals("admin")) {
			try {
				productRepository.deleteById(id);
			} catch (Exception e) {
				throw new ProductNotFoundException(id);
			}
		} else {
			try {
				Product product = productRepository.findById(id).get();
				product.setWithdrawn(true);
				productRepository.save(product);
			} catch (Exception e) {
				throw new ProductNotFoundException(id);
			}
		}
		return "{\"message\": \"OK\" }";
	}

	// returns JSON with all shops
	@GetMapping(path = "/shops", produces = "application/json")
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
	@GetMapping(path = "/shops/name/{name}", produces = "application/json")
	String shopsLike(@PathVariable String name, @RequestParam Map<String, String> params)
			throws JsonProcessingException {

		Integer start, count;
		String format, newName, status;
		start   = (params.get("start")  == null) ? 0         : Integer.valueOf(params.get("start"));
		count   = (params.get("count")  == null) ? 20        : Integer.valueOf(params.get("count"));
		status  = (params.get("status") == null) ? "ACTIVE"  : params.get("status");
		newName = (name                 == null) ? ""        : name;
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
	@PostMapping(path = "/shops", produces = "application/json")
	ShopDTO newShop(@ModelAttribute("shop") ShopDTO newShop, @RequestHeader("X-OBSERVATORY-AUTH") String token,
			@RequestParam Map<String, String> params) {

		String format = (params.get("format") == null) ? "json" : params.get("format");
		if (token == null)
			throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
		String role = determineRole(token);

		if (!format.equals("json"))
			throw new BadRequestException("Error: Invalid format");
		if (newShop.anyNull())
			throw new BadRequestException("Error: Invalid shop field(s)");

		return shopRepository.save(newShop._convertToShop())._convertToShopDTO();
	}

	// returns JSON with single shop
	@GetMapping(path = "/shops/{id}", produces = "application/json")
	ShopDTO oneShop(@PathVariable Integer id, @RequestParam Map<String, String> params) {

		String format = (params.get("format") == null) ? "json" : params.get("format");
		if (!format.equals("json"))
			throw new BadRequestException("Error: Invalid format");

		return shopRepository.findById(id).orElseThrow(() -> new ShopNotFoundException(id))._convertToShopDTO();
	}

	// replaces shop
	@PutMapping(path = "/shops/{id}", produces = "application/json")
	ShopDTO replaceShop(@ModelAttribute("shop") ShopDTO newShop, @PathVariable Integer id,
			@RequestHeader("X-OBSERVATORY-AUTH") String token, @RequestParam Map<String, String> params) {

		String format = (params.get("format") == null) ? "json" : params.get("format");
		if (token == null)
			throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
		String role = determineRole(token);

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
	@PatchMapping(path = "/shops/{id}", produces = "application/json")
	ShopDTO fixShop(@ModelAttribute("shop") ShopDTO newShop, @PathVariable Integer id,
			@RequestHeader("X-OBSERVATORY-AUTH") String token, @RequestParam Map<String, String> params) {

		String format = (params.get("format") == null) ? "json" : params.get("format");
		if (token == null)
			throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
		String role = determineRole(token);

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
	@DeleteMapping(path = "/shops/{id}", produces = "application/json")
	String deleteShop(@PathVariable Integer id, @RequestHeader("X-OBSERVATORY-AUTH") String token,
			@RequestParam Map<String, String> params) {

		String format = (params.get("format") == null) ? "json" : params.get("format");
		if (token == null)
			throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
		String role = determineRole(token);

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

	// returns JSON with all prices
	@GetMapping(path = "/prices", produces = "application/json")
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
				prices = priceRepository.findByShopIdInAndProductIdInAndDateBetween(shops, products, fromParam, toParam);
			else if (shops != null && products == null)
				prices = priceRepository.findByShopIdInAndDateBetween(shops, fromParam, toParam);
			else if (shops == null && products != null)
				prices = priceRepository.findByProductIdInAndDateBetween(products, fromParam, toParam);
			else
				prices = priceRepository.findByDateBetween(fromParam, toParam);

			for (Price p : prices) {
				// Find if there is an intersection between the input and the tags found
				if (tags != null)
					inputTags = new HashSet<>(tags);
				else
					inputTags = null;

				Product productTemp = productRepository.findById(p.getProductId()).get();
				List<ProductTag> productTagTemp = productTemp.getTags();
				Set<String> pTags = productTagTemp.stream().map(ProductTag::getName).collect(toSet());

				Shop shopTemp = shopRepository.findById(p.getShopId()).get();
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
	@PostMapping(path = "/prices", produces = "application/json")
	String newPrice(@RequestHeader("X-OBSERVATORY-AUTH") String token, @RequestParam Map<String, String> params)
			throws JsonProcessingException, ParseException{

		PriceDTO newPrice = new PriceDTO(Double.valueOf(params.get("price")), params.get("dateFrom"),
				params.get("dateTo"), Integer.valueOf(params.get("productId")), Integer.valueOf(params.get("shopId")));

		String format = (params.get("format") == null) ? "json" : params.get("format");
		if (token == null)
			throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
		String role = determineRole(token);

		if (!format.equals("json"))
			throw new BadRequestException("Error: Invalid format");
		if (newPrice.anyNull())
			throw new BadRequestException("Invalid price field(s)");

		Product productTemp = productRepository.findById(Integer.valueOf(params.get("productId"))).get();
		List<ProductTag> productTagTemp = productTemp.getTags();
		List<String> pTags = productTagTemp.stream().map(ProductTag::getName).collect(toList());

		Shop shopTemp = shopRepository.findById(Integer.valueOf(params.get("shopId"))).get();
		List<ShopTag> shopTagTemp = shopTemp.getTags();
		List<String> sTags = shopTagTemp.stream().map(ShopTag::getName).collect(toList());

		List<PriceFinal> finalPrices = new ArrayList<>();
		String from = newPrice.getDateFrom();
		String to = addOneDay(newPrice.getDateTo());
		PriceDTO p;
		do{
			p = priceRepository.save(newPrice._convertToPrice())._convertToPriceDTO();

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

	// returns JSON with all users
	@GetMapping(path = "/users", produces = "application/json")
	String allUsers(@RequestParam Map<String, String> params,
			@RequestHeader("X-OBSERVATORY-AUTH") String token) throws JsonProcessingException {

		Integer start, count;
		String format;
		start  = (params.get("start")  == null) ? 0         : Integer.valueOf(params.get("start"));
		count  = (params.get("count")  == null) ? 20        : Integer.valueOf(params.get("count"));
		format = (params.get("format") == null) ? "json"    : params.get("format");
		
		if (token == null)
			throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
		String role = determineRole(token);
		if (!role.equals("admin"))
			throw new UnauthorizedException("Error: Only admins can perform this action");

		List<User> users;
		List<UserDTO> userDTOs = new ArrayList<>();
		boolean validStart = true, validCount = true, validFormat = true;

		if (start < 0)
			validStart = false;
		if (count <= 0)
			validCount = false;
		if (!format.equals("json"))
			validFormat = false;


		if (validStart && validCount && validFormat) {
			users = userRepository.findAll();
			users.sort(Comparator.comparing(User::getUsername));

			for (int i = start; i <= start+count-1 && i < users.size(); i++) {
				userDTOs.add(users.get(i)._convertToUserDTO());
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			String json = mapper.writeValueAsString(
					new UserJson(start, count, Long.valueOf(userDTOs.size()), userDTOs));

			return json;
		} else {
			StringBuilder builder = new StringBuilder("Error: ");
			if (!validStart)
				builder.append("Invalid start parameter, ");
			if (!validCount)
				builder.append("Invalid count parameter, ");
			if (!validFormat)
				builder.append("Invalid format, ");
			builder.setLength(builder.length() - 2);
			throw new BadRequestException(builder.toString());
		}
	}

	// creates new user
	@PostMapping(path = "/users", produces = "application/json")
	UserDTO newUser(@ModelAttribute("user") UserDTO newUser,
			@RequestParam Map<String, String> params) {

		String format = (params.get("format") == null) ? "json" : params.get("format");

		if (!format.equals("json"))
			throw new BadRequestException("Error: Invalid format");

		if (newUser.anyNull())
			throw new BadRequestException("Error: Invalid user field(s)");

		String encrypted = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt(10));
		newUser.setPassword(encrypted);
		return userRepository.save(newUser._convertToUser())._convertToUserDTO();
		}

	// deletes user
	@DeleteMapping(path = "/users/{id}", produces = "application/json")
	String deleteUser(@PathVariable Integer id, @RequestHeader("X-OBSERVATORY-AUTH") String token,
			@RequestParam Map<String, String> params) {

		String format = (params.get("format") == null) ? "json" : params.get("format");
		if (token == null)
			throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
		String role = determineRole(token);

		if (!format.equals("json"))
			throw new BadRequestException("Error: Invalid format");

		if (role.equals("admin")) {
			Optional<User> user = userRepository.findById(id);
			if (user.isPresent()){
				if (!user.get().getRole().equals("admin"))
					userRepository.deleteById(id);
				else
					throw new UnauthorizedException("Error: Cannot delete admin user");
			} else {
				throw new UserNotFoundException(id);
			}
			return "{\"message\": \"OK\" }";
		} else {
			throw new UnauthorizedException("Error: Only admins can perform this action");
		}
	}
}