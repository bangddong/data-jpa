package stduy.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import stduy.datajpa.dto.MemberDto;
import stduy.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

	@Query(name = "Member.findByUsername")
	List<Member> findByUsername(@Param("username") String username);

	@Query("select m from Member m where m.username = :username and m.age = :age")
	List<Member> findUser(@Param("username") String username, @Param("age") int age);

	@Query("select m.username from Member m")
	List<String> findUsernameList();

	@Query("select new stduy.datajpa.dto.MemberDto(m.id, m.username, t.name)"
		+ " from Member m join m.team t")
	List<MemberDto> findMemberDto();

	@Query("select m from Member m where m.username in :names")
	List<Member> findByNames(@Param("names") Collection<String> names);

	Member findMemberByUsername(String username);

	List<Member> findListByUsername(String username);

	Optional<Member> findOptionalByUsername(String username);

	Page<Member> findByAge(int age, Pageable pageable);

	@Query(value = "select m from Member m",
			countQuery = "select count(m.username) from Member m")
	Page<Member> findMemberAllCountBy(Pageable pageable);
}
