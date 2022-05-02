package priceobservatory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class ShopTag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Integer id;
	private String name;
	@JsonIgnore
	@ManyToMany(mappedBy = "tags")
	private List<Shop> shops;

	public ShopTag() {}

	public ShopTag(String name) {
		this.name = name;
	}
}
