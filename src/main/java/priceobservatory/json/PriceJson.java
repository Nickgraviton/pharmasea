package priceobservatory.json;

import lombok.Data;
import lombok.NonNull;
import priceobservatory.dto.PriceFinal;

import java.util.List;

@Data
public class PriceJson {
	@NonNull
	private Integer start;
	@NonNull
	private Integer count;
	@NonNull
	private Long total;
	@NonNull
	private List<PriceFinal> prices;
}
