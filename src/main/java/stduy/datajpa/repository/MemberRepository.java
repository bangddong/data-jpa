package stduy.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import stduy.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
