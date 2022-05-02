package priceobservatory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShopNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ShopNotFoundException(Integer id) {
		super("Shop " + id + " not found");
	}
}
