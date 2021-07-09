package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 회원 저장
            // Member member = new Member();
            // member.setId(2L);
            // member.setName("서정민");
            //
            // em.persist(member);

            // 회원 수정
            // Member findMember = em.find(Member.class, 1L);
            // findMember.setName("HelloJPA");

            // 회원 조회
            em.createQuery("select m from Member m", Member.class)
                    .getResultList().stream()
                    .map(Member::getName)
                    .forEach(System.out::println);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
