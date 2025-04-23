package stduy.datajpa.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import stduy.datajpa.entity.Member;
import stduy.datajpa.entity.Team;

public class MemberSpec {

	public static Specification<Member> teamName(final String teamName) {
		return (root, query, criteriaBuilder) -> {
			if (StringUtils.isEmpty(teamName)) {
				return null;
			}

			Join<Object, Team> t = root.join("team", JoinType.INNER); // 회원과 조인
			return criteriaBuilder.equal(t.get("name"), teamName);
		};
	}

	public static Specification<Member> username(final String username) {
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.equal(root.get("username"), username);
	}
}
