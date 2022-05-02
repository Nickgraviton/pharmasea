package priceobservatory.dto;

import lombok.Data;
import priceobservatory.model.Product;
import priceobservatory.model.ProductTag;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductDTO {
	private Integer id;
	private String name;
	private String description;
	private String category;
	private List<String> tags;
	private Boolean withdrawn;

	public ProductDTO() {}

	public ProductDTO (Integer id, String name, String description, String category, List<String> tags, Boolean withdrawn) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
		this.tags = tags;
		this.withdrawn = withdrawn;
	}

	public Boolean anyNull() {
		if (name        == null) return true;
		if (description == null) return true;
		if (category    == null) return true;
		if (tags        == null) return true;
		if (withdrawn   == null) return true;
		return false;
	}

	public Product _convertToProduct() {
		List<ProductTag> tagList = new ArrayList<>();
		for (String s : tags) {
			tagList.add(new ProductTag(s));
		}
		return new Product(name, description, category,
				tagList, withdrawn);
	}
}
