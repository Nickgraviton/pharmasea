package priceobservatory.dto;

import lombok.Data;
import priceobservatory.exception.BadRequestException;
import priceobservatory.model.Shop;
import priceobservatory.model.ShopTag;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShopDTO {
	private Integer id;
	private String name;
	private String address;
	private Double lng;
	private Double lat;
	private List<String> tags;
	private Boolean withdrawn;

	public ShopDTO() {}

	public ShopDTO (Integer id, String name, String address, Double lng, Double lat, List<String> tags, Boolean withdrawn) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.lng = lng;
		this.lat = lat;
		this.tags = tags;
		this.withdrawn = withdrawn;
	}

	public Boolean anyNull() {
		if (name      == null) throw new BadRequestException("name");
		if (address   == null) throw new BadRequestException("address");
		if (lng       == null) throw new BadRequestException("lng");
		if (lat       == null) throw new BadRequestException("lat");
		if (tags      == null) throw new BadRequestException("tags");
		if (withdrawn == null) throw new BadRequestException("withdrawn");
		return false;
	}

	public Shop _convertToShop() {
		List<ShopTag> tagList = new ArrayList<ShopTag>();
		for (String s : tags) {
			tagList.add(new ShopTag(s));
		}
		return new Shop(name, address, lng, lat,
				tagList, withdrawn);
	}
}
