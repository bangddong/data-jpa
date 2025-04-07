package stduy.datajpa.dto;

import lombok.Data;
import stduy.datajpa.entity.Member;

@Data
public class MemberDto {

	private Long id;
	private String username;

	public MemberDto(Long id, String username) {
		this.id = id;
		this.username = username;
	}

	public MemberDto(Member member) {
		this.id = member.getId();
		this.username = member.getUsername();
	}
}
