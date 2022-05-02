package priceobservatory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ProductNotFoundException(Integer id) {
		super("Product " + id + " not found");
	}
}
