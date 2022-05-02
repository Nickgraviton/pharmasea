package priceobservatory.model;

import lombok.Data;
import priceobservatory.dto.ProductDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Product {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String description;
	private String category;
	@ManyToMany(cascade = {CascadeType.ALL})
	@JoinTable
	(
	 name = "has_product_tag",
	 joinColumns = @JoinColumn(name = "P_id"),
	 inverseJoinColumns = @JoinColumn(name = "T_id")
	)
	private List<ProductTag> tags;
	private Boolean withdrawn;

	public Product() {}

	public Product (String name, String description, String category, List<ProductTag> tags, Boolean withdrawn) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.tags = tags;
		this.withdrawn = withdrawn;
	}

	public ProductDTO _convertToProductDTO() {
		List<String> tagList = new ArrayList<String>();
		for (ProductTag t: tags) {
			tagList.add(t.getName());
		}
		return new ProductDTO(id, name, description, category,
				tagList, withdrawn);
	}

	public void setTagsFromString(List<String> tags) {
		this.tags.clear();
		for (String s: tags) {
			this.tags.add(new ProductTag(s));
		}
	}
}
