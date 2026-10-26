package pl.lodz.p.repository;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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
                transaction.commit();
                return guestEnt;
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw new RepositoryException("Wystąpił błąd przy zapisie gościa o personalId = " + guestEnt.getPersonalId() + ". " + e.getMessage(), e);
            }

        }
    }

    public void setGuestArchivedStatus(String personalId, boolean isArchived) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();

            try {
                transaction.begin();

                GuestEnt foundGestEnt = findGuestByPersonalId(personalId, em);
                foundGestEnt.setArchived(isArchived);

                transaction.commit();
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw new RepositoryException("Wystąpił błąd przy zmianie statusu archiwizacji gościa o personalId = " + personalId + ", " + e.getMessage(), e);
            }

        }
    }

    private GuestEnt findGuestByPersonalId(String personalId, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GuestEnt> cq = cb.createQuery(GuestEnt.class);
        Root<GuestEnt> table = cq.from(GuestEnt.class);

        cq.select(table).where(cb.equal(table.get("personalId"), personalId));

        GuestEnt foundGestEnt = em.createQuery(cq)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono gościa o personalId = " + personalId));

        return foundGestEnt;
    }

    public GuestEnt findGuestByPersonalId(String personalId) {
        try (EntityManager em = emf.createEntityManager()) {
            return findGuestByPersonalId(personalId, em);
        }
    }

    public List<GuestEnt> findAllGuests() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<GuestEnt> cq = cb.createQuery(GuestEnt.class);
            Root<GuestEnt> table = cq.from(GuestEnt.class);

            cq.select(table);

            return em.createQuery(cq).getResultList();
        }
    }
}
