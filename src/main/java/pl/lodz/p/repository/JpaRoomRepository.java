package pl.lodz.p.repository;

import jakarta.persistence.*;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.RoomEnt;

import java.util.List;

public class JpaRoomRepository {
    private final EntityManagerFactory emf;

    public JpaRoomRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public RoomEnt saveRoom(RoomEnt roomEnt) {

        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();

            try {
                transaction.begin();
                em.persist(roomEnt);
                transaction.commit();
                return roomEnt;
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw new RepositoryException("Wystąpił błąd przy zapisie pokoju o numerze = " + roomEnt.getNumber() + ". " + e.getMessage());
            }

        }
    }

    public void setRoomArchivedStatus(String number, boolean isArchived) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();

            try {
                transaction.begin();

                RoomEnt foundRoomEnt = findRoomByNumber(number, em);
                em.lock(foundRoomEnt, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                foundRoomEnt.setArchived(isArchived);

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
                    throw new ConcurrentOperationException("Wykryto współbieżną modyfikację pokoju o numerze = " + number + ", " + e.getMessage());
                } else {
                    throw new RepositoryException("Wystąpił błąd przy zmianie statusu archiwizacji pokoju o numerze = " + number + ", " + e.getMessage());
                }

            }

        }
    }
    private RoomEnt findRoomByNumber(String number, EntityManager em) {
        return em.createQuery("SELECT r FROM Room r WHERE r.number = :number", RoomEnt.class)
                .setParameter("number", number)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono pokoju o numerze = " + number));
    }

    public RoomEnt findRoomByNumber(String number) {
        try (EntityManager em = emf.createEntityManager()) {
            return findRoomByNumber(number, em);
        }
    }

    public List<RoomEnt> findAllRooms() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT r FROM Room r", RoomEnt.class)
                    .getResultList();
        }
    }
}
