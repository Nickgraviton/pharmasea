package priceobservatory.json;

import lombok.Data;
import lombok.NonNull;
import priceobservatory.dto.ShopDTO;

import java.util.List;

@Data
public class ShopJson {
	@NonNull
	private Integer start;
	@NonNull
	private Integer count;
	@NonNull
	private Long total;
	@NonNull
	private List<ShopDTO> shops;
}
