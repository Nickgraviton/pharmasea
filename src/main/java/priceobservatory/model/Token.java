package priceobservatory.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Token {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	@Column(name="role")
	private String role;

    @Column(name="token")
    private String token;

    public Token() {}

    public Token (String role, String token) {
        this.role = role;
        this.token = token;
    }
}
