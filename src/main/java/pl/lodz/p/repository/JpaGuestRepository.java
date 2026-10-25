package pl.lodz.p.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.GuestEnt;

public class JpaGuestRepository {
    private final EntityManagerFactory emf;

    public JpaGuestRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public GuestEnt saveGuest(GuestEnt guestEnt) {

        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();

            try {
                transaction.begin();
                em.persist(guestEnt.getGuestType());
                em.persist(guestEnt);
                transaction.commit();
                return guestEnt;
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw new RepositoryException("Wystąpił błąd przy zapisie gościa, o uuid: " + guestEnt.getUuid() + ", do bazy danych", e);
            }

        }
    }

    public void archiveGuest(GuestEnt guestEnt) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();

            try {
                transaction.begin();
                em.merge(guestEnt);
                transaction.commit();
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw new RepositoryException("Wystąpił błąd przy archiwizowaniu gościa, o uuid: " + guestEnt.getUuid() + ", do bazy danych", e);
            }

        }
    }
}
