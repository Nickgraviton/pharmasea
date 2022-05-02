package priceobservatory.json;

import lombok.Data;
import lombok.NonNull;
import priceobservatory.dto.UserDTO;

import java.util.List;

@Data
public class UserJson {
	@NonNull
	private Integer start;
	@NonNull
	private Integer count;
	@NonNull
	private Long total;
	@NonNull
	private List<UserDTO> users;
}