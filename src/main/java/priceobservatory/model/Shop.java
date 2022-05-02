package priceobservatory.model;

import lombok.Data;
import priceobservatory.dto.ShopDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Shop {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String address;
	private Double lng;
	private Double lat;
	@ManyToMany(cascade = {CascadeType.ALL})
	@JoinTable
	(
	 name = "has_shop_tag",
	 joinColumns = @JoinColumn(name = "S_id"),
	 inverseJoinColumns = @JoinColumn(name = "T_id")
	)
	private List<ShopTag> tags;
	private Boolean withdrawn;

	public Shop() {}

	public Shop (String name, String address, Double lng, Double lat, List<ShopTag> tags, Boolean withdrawn) {
		this.name = name;
		this.address = address;
		this.lng = lng;
		this.lat = lat;
		this.tags = tags;
		this.withdrawn = withdrawn;
	}

	public ShopDTO _convertToShopDTO() {
		List<String> tagList = new ArrayList<String>();
		for (ShopTag t: tags) {
			tagList.add(t.getName());
		}
		return new ShopDTO(id, name, address, lng, lat,
				tagList, withdrawn);
	}

	public void setTagsFromString(List<String> tags) {
		this.tags.clear();
		for (String s: tags) {
			this.tags.add(new ShopTag(s));
		}
	}
}
