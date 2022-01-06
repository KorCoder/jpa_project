package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
            Team team = new Team();
            team.setName("TeamA");
//            team.getMembers().add(member);
            em.persist(team);

            Member member = new Member();
            member.setUsername("member10");
            // 연관관계 주인인 Member에서 team 객체 설정
            member.setTeam(team);
            em.persist(member);

            // 순수한 객체 관계를 고려하여 비주인인 team 객체에도 값을 입력한다. (원래는 안해도 됨)
            // 그러나 양쪽에서 까먹고 한쪽을 안할수 있으므로
            // meber 객체 (주인 객체) 에서 연관관계 매핑 시 주인 아닌쪽에도 자동 매핑해주는 코드를 작성하는게 좋다.
            team.getMembers().add(member);

            // 아래 두줄 실행을 안하면 db에 저장되지 않기때문에 영속성 컨텍스트 1차 캐시 에서는 연관관계가 맺어지지 않는다.
            em.flush();
            em.clear();

            // team 테이블 조회 쿼리 실행 됨
            Team findTeam = em.find(Team.class, team.getId());
            // member 테이블 조회 쿼리 실행 됨
            List<Member> findMembers = findTeam.getMembers();

            for (Member m : findMembers ) {
                System.out.println(" m = " + m.getUsername());
            }

            tx.commit();
        } catch(Exception e){
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}

