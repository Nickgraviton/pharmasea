package priceobservatory.dto;

import lombok.Data;
import priceobservatory.model.User;

@Data
public class UserDTO {
    private Integer id;
	private String LName;
	private String FName;
	private String username;
	private String email;
	private String password;
	private String role;

	public UserDTO() {}

	public UserDTO (Integer id, String LName, String FName, String username, String email, String password, String role) {
		this.id = id;
		this.LName = LName;
		this.FName = FName;
		this.username = username;
		this.email = email;
        this.password = password;
        this.role = role;
    }

	public Boolean anyNull() {
		if (LName    == null) return true;
		if (FName    == null) return true;
		if (username == null) return true;
        if (email    == null) return true;
        if (password == null) return true;
		return false;
	}

	public User _convertToUser() {
		return new User(LName, FName, username,
            email, password, "user");
	}
}
