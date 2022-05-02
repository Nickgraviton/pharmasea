package priceobservatory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class ProductTag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Integer id;
	private String name;
	@JsonIgnore
	@ManyToMany(mappedBy = "tags")
	private List<Product> products;

	public ProductTag() {}

	public ProductTag(String name) {
		this.name = name;
	}
}
