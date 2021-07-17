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
            Member member1 = new Member();
            member1.setUsername("member1");
            em.persist(member1);

            em.flush();
            em.clear();

            Member m1 = em.getReference(Member.class, member1.getId());
            System.out.println("m1.getUsername() = " + m1.getClass());

            Member reference = em.find(Member.class, member1.getId());
            System.out.println("reference = " + reference.getClass());

            System.out.println("a == a: " + (m1 == reference));

            // Member findMember = em.find(Member.class, member.getId());
            // Member findMember = em.getReference(Member.class, member1.getId());
            //
            // System.out.println("findMember = " + findMember.getClass());
            // System.out.println("findMember.getId() = " + findMember.getId());
            // System.out.println("findMember.getUsername() = " + findMember.getUsername());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
