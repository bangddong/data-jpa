package stduy.datajpa.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import stduy.datajpa.dto.MemberDto;
import stduy.datajpa.entity.Member;
import stduy.datajpa.entity.Team;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	EntityManager em;

	@Test
	public void testMember() {
		Member member = new Member("memberA");
		Member savedMember = memberRepository.save(member);

		Member findMember = memberRepository.findById(savedMember.getId()).get();

		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member);
	}
	@Test
	public void baseCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member1");
		memberRepository.save(member1);
		memberRepository.save(member2);

		// 단건 조회 검증
		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);

		// 리스트 조회 검증
		List<Member> all = memberRepository.findAll();
		assertThat(all.size()).isEqualTo(2);

		// 카운트 검증
		long count = memberRepository.count();
		assertThat(count).isEqualTo(2);

		// 삭제 검증
		memberRepository.delete(member1);
		memberRepository.delete(member2);

		long deleteCount = memberRepository.count();
		assertThat(deleteCount).isEqualTo(0);
	}

	@Test
	public void testNamedQuery() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findByUsername("AAA");
		Member findMember = result.get(0);
		assertThat(findMember).isEqualTo(m1);
	}

	@Test
	public void testQuery() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findUser("AAA", 10);
		assertThat(result.get(0)).isEqualTo(m1);
	}

	@Test
	public void findUsernameList() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<String> usernameList = memberRepository.findUsernameList();
		for (String s : usernameList) {
			System.out.println("s = " + s);
		}
	}


	@Test
	public void findMemberDto() {
		Team team = new Team("teamA");
		teamRepository.save(team);

		Member m1 = new Member("AAA", 10);
		m1.setTeam(team);
		memberRepository.save(m1);

		List<MemberDto> memberDto = memberRepository.findMemberDto();
		for (MemberDto dto : memberDto) {
			System.out.println("dto = " + dto);
		}
	}

	@Test
	public void findByNames() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
		for (Member member : result) {
			System.out.println("member = " + member);
		}
	}

	@Test
	public void returnType() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findListByUsername("AAA");
		System.out.println("aaa = " + result);
	}

	@Test
	public void paging() throws Exception {
		// given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 10));
		memberRepository.save(new Member("member3", 10));
		memberRepository.save(new Member("member4", 10));
		memberRepository.save(new Member("member5", 10));

		int age = 10;
		PageRequest pageRequest = PageRequest.of(
			0, 3, Sort.by(Sort.Direction.DESC, "username")
		);

		// when
		Page<Member> page = memberRepository.findByAge(age, pageRequest);
		Page<MemberDto> toMap = page.map(
			member -> new MemberDto(member.getId(), member.getUsername())
		);

		// then
		List<Member> content = page.getContent(); // 조회된 데이터
		assertThat(content.size()).isEqualTo(3); // 조호된 데이터 수
		assertThat(page.getTotalElements()).isEqualTo(5); // 전체 데이터 수
		assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
		assertThat(page.getTotalPages()).isEqualTo(2); // 전체 페이지 번호
		assertThat(page.isFirst()).isTrue(); // 첫 번째 항목인가?
		assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는가?
	}

	@Test
	public void bulkUpdate() throws Exception {
		// given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 19));
		memberRepository.save(new Member("member3", 20));
		memberRepository.save(new Member("member4", 21));
		memberRepository.save(new Member("member5", 40));

		// when
		int resultCount = memberRepository.bulkAgePlus(20);

		// then
		assertThat(resultCount).isEqualTo(3);
	}

	@Test
	public void findMemberLazy() throws Exception {
		//given
		//member1 -> teamA
		//member2 -> teamB
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);
		memberRepository.save(new Member("member1", 10, teamA));
		memberRepository.save(new Member("member2", 20, teamB));
		em.flush();
		em.clear();
		//when
		List<Member> members = memberRepository.findEntityGraphByUsername("member1");
		//then
		for (Member member : members) {
			member.getTeam().getName();
			System.out.println("member = " + member.getTeam().getClass());
		}
	}

	@Test
	public void queryHint() throws Exception {
	    // given
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush();
		em.clear();

	    // when
		Member findMember = memberRepository.findReadOnlyByUsername("member1");
		findMember.setUsername("member2");

		em.flush();

	    // then
	}

	@Test
	public void lock() throws Exception {
	    // given
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush();
		em.clear();

	    // when
		List<Member> result = memberRepository.findLockByUsername("member1");

	    // then
	}

	@Test
	public void callCustom() throws Exception {
	    // given
		List<Member> result = memberRepository.findMemberCustom();

		// when

	    // then
	}

	@Test
	public void specBasic() throws Exception {
	    // given
		Team teamA = new Team("teamA");
		em.persist(teamA);

		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);

		em.flush();
		em.clear();

		// when
		Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
		List<Member> result = memberRepository.findAll(spec);

		// then
		Assertions.assertThat(result.size()).isEqualTo(1);
	}

	@Test
	public void queryByExample() throws Exception {
	    // given
		Team teamA = new Team("teamA");
		em.persist(teamA);

		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);

		em.flush();
		em.clear();

		// when

	    // then
		// Probe
		Member member = new Member("m1");
		Team team = new Team("teamA");
		member.setTeam(team);

		ExampleMatcher matcher = ExampleMatcher.matching()
			.withIgnorePaths("age");

		Example<Member> example = Example.of(member, matcher);

		memberRepository.findAll(example);

		List<Member> result = memberRepository.findAll(example);

		assertThat(result.get(0).getUsername()).isEqualTo("m1");
	}

	@Test
	public void projections() throws Exception {
	    // given
		Team teamA = new Team("teamA");
		em.persist(teamA);

		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);

		em.flush();
		em.clear();

	    // when
		List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

		for (NestedClosedProjections usernameOnly: result) {
			System.out.println("usernameOnly = " + usernameOnly.getUsername());
		}

		// then
	}

	@Test
	public void nativeQuery() throws Exception {
	    // given
		Team teamA = new Team("teamA");
		em.persist(teamA);

		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);

		em.flush();
		em.clear();

	    // when
		Member result = memberRepository.findByNativeQuery("m1");
		System.out.println("result = " + result);

		// then
	}

	@Test
	public void nativeQueryWithProjection() throws Exception {
	    // given
		Team teamA = new Team("teamA");
		em.persist(teamA);

		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);

		em.flush();
		em.clear();

	    // when
		Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
		List<MemberProjection> content = result.getContent();
		for (MemberProjection memberProjection : content) {
			System.out.println("memberProjection = " + memberProjection.getUsername());
			System.out.println("memberProjection = " + memberProjection.getTeamName());
		}

		// then
	}

}