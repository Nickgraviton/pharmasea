package priceobservatory.model;

import lombok.Data;
import priceobservatory.dto.UserDTO;

import javax.persistence.*;

@Data
@Entity
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	@Column(name="LName")
	private String LName;

	@Column(name="FName")
	private String FName;

	@Column(name="Username")
	private String username;

	@Column(name="Email")
	private String email;

	@Column(name="Password")
	private String password;

	@Column(name="role")
	private String role;

	public User() {}

	public User (String LName, String FName, String username, String email, String password, String role) {
		this.username = username;
		this.LName = LName;
		this.FName = FName;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	public UserDTO _convertToUserDTO() {
		return new UserDTO(id, LName, FName, username,
            email, password, role);
	}
}
