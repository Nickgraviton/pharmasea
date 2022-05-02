package priceobservatory.dto;

import lombok.Data;
import priceobservatory.exception.BadRequestException;
import priceobservatory.model.Price;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class PriceDTO {
	private Integer id;
	private Double price;
	private String dateFrom;
	private String dateTo;
	private Integer productId;
	private Integer shopId;

	public PriceDTO() {}

	public PriceDTO (Integer id, Double price, String dateFrom, Integer productId, Integer shopId) {
		this.id = id;
		this.price = price;
		this.dateFrom = dateFrom;
		this.productId = productId;
		this.shopId = shopId;
	}

	public PriceDTO (Double price, String dateFrom, String dateTo, Integer productId, Integer shopId) {
		this.price = price;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.productId = productId;
		this.shopId = shopId;
	}

	public boolean anyNull() {
		if (price     == null) return true;
		if (dateFrom  == null) return true;
		if (dateTo    == null) return true;
		if (productId == null) return true;
		if (shopId    == null) return true;
		return false;
	}

	public Price _convertToPrice() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date dateF = null;
		Date dateT = null;
		try {
			dateF = formatter.parse(dateFrom); //(pattern = "yyyy-MM-dd")
			dateT = formatter.parse(dateTo);
		} catch(ParseException p) {
			throw new BadRequestException("Error: Invalid date format");
		}
		return new Price(price, productId, shopId, dateF);
	}
}
