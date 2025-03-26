package stduy.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter // 실습을 위한 Setter
public class Member {

	@Id
	@GeneratedValue
	private Long id;

	private String username;

	protected Member() {
	}

	public Member(String username) {
		this.username = username;
	}
}
