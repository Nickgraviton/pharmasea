package priceobservatory.json;

import lombok.Data;
import lombok.NonNull;
import priceobservatory.dto.ProductDTO;

import java.util.List;

@Data
public class ProductJson {
	@NonNull
	private Integer start;
	@NonNull
	private Integer count;
	@NonNull
	private Long total;
	@NonNull
	private List<ProductDTO> products;
}
