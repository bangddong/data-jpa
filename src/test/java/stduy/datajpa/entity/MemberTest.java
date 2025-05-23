package stduy.datajpa.entity;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import stduy.datajpa.repository.MemberRepository;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

	@PersistenceContext
	EntityManager em;

	@Autowired
	MemberRepository memberRepository;

	@Test
	public void testEntity() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		em.persist(teamA);
		em.persist(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member1", 10, teamA);
		Member member3 = new Member("member1", 10, teamA);
		Member member4 = new Member("member1", 10, teamA);
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);

		// 초기화
		em.flush();
		em.clear();

		// 확인
		List<Member> members = em.createQuery("select m from Member m", Member.class)
			.getResultList();

		for (Member member : members) {
			System.out.println("member = " + member);
			System.out.println("-> member.team = " + member.getTeam());
		}
	}

	@Test
	public void JpaEventBaseEntity() throws Exception {
	    // given
		Member member = new Member("member1");
		memberRepository.save(member); // @PrePersist

		Thread.sleep(100);
		member.setUsername("member2");

		em.flush(); // @PreUpdate
		em.clear();

		// when
		Member findMember = memberRepository.findById(member.getId()).get();

		// then
		System.out.println("findMember.createDate = " + findMember.getCreateData());
		System.out.println("findMember.updateDate = " + findMember.getLastModifiedDate());
		System.out.println("findMember.createBy = " + findMember.getCreateBy());
		System.out.println("findMember.lastModifiedBy = " + findMember.getLastModifiedBy());
	}

}