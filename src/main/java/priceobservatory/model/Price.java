package priceobservatory.model;

import lombok.Data;
import priceobservatory.dto.PriceDTO;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Entity
public class Price {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="price")
	private Double price;

	@Column(name="P_id")
	private Integer productId;

	@Column(name="S_id")
	private Integer shopId;

	@Column(name="date")
	private Date date;

	public Price() {}

	public Price (Double price, Integer productId, Integer shopId, Date date) {
		this.price = price;
		this.productId = productId;
		this.shopId = shopId;
		this.date = date;
	}

	public PriceDTO _convertToPriceDTO() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return new PriceDTO(id, price, formatter.format(date), productId, shopId);
	}
}
