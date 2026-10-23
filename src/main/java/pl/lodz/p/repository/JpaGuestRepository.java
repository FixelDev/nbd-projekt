package pl.lodz.p.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import pl.lodz.p.model.db.GuestEnt;

public class JpaGuestRepository {
    private final EntityManagerFactory emf;

    public JpaGuestRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(GuestEnt guestEnt) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        em.persist(guestEnt.getGuestType());
        em.persist(guestEnt);
        transaction.commit();
    }
}
