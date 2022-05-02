package priceobservatory.dto;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
// Price to be returned to the user
public class PriceFinal {
	private Double price;
	private Integer productId;
	private Integer shopId;
	private String date;

	private String productName;
	private List<String> productTags;
	private String shopName;
	private List<String> shopTags;
	private String shopAddress;
	private Integer shopDist;

	public PriceFinal(){}

	public void setDate (Date date) {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		this.date = simpleDateFormat.format(date);
	}

	public void setDateFromString(String date) {
		this.date = date;
	}
}
