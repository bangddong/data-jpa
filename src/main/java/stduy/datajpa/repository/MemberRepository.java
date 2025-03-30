package stduy.datajpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import stduy.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
}
