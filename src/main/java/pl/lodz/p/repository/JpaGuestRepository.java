package pl.lodz.p.repository;

import jakarta.persistence.*;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.GuestEnt;

import java.util.List;

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
                IO.println("TUTAJJJJ: " + guestEnt.getLastName());
                transaction.commit();
                return guestEnt;
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw new RepositoryException("Wystąpił błąd przy zapisie gościa o personalId = " + guestEnt.getPersonalId() + ". " + e.getMessage());
            }

        }
    }

    public void setGuestArchivedStatus(String personalId, boolean isArchived) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();

            try {
                transaction.begin();


                GuestEnt foundGestEnt = findGuestByPersonalId(personalId, em);
                em.lock(foundGestEnt, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                foundGestEnt.setArchived(isArchived);

                transaction.commit();
            } catch (EntityNotFoundException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw e;
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                if (e instanceof OptimisticLockException || e.getCause() instanceof OptimisticLockException) {
                    throw new ConcurrentOperationException("Wykryto współbieżną modyfikację gościa o personalId = " + personalId + ", " + e.getMessage());
                } else {
                    throw new RepositoryException("Wystąpił błąd przy zmianie statusu archiwizacji gościa o personalId = " + personalId + ", " + e.getMessage());
                }

            }

        }
    }

    private GuestEnt findGuestByPersonalId(String personalId, EntityManager em) {
        return em.createQuery("SELECT g FROM Guest g WHERE g.personalId = :personalId", GuestEnt.class)
                .setParameter("personalId", personalId)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono gościa o personalId = " + personalId));
    }

    public GuestEnt findGuestByPersonalId(String personalId) {
        try (EntityManager em = emf.createEntityManager()) {
            return findGuestByPersonalId(personalId, em);
        }
    }

    public List<GuestEnt> findAllGuests() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT g FROM Guest g", GuestEnt.class)
                    .getResultList();
        }
    }
}
