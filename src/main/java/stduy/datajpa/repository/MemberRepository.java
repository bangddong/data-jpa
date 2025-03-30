package stduy.datajpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import stduy.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

	@Query(name = "Member.findByUsername")
	List<Member> findByUsername(@Param("username") String username);

	@Query("select m from Member m where m.username = :username and m.age = :age")
	List<Member> findUser(@Param("username") String username, @Param("age") int age);

	@Query("select m.username from Member m")
	List<String> findUsernameList();

}
