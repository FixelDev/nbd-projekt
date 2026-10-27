package pl.lodz.p.repository;

import jakarta.persistence.*;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.GuestEnt;
import pl.lodz.p.model.db.RentEnt;
import pl.lodz.p.model.db.RoomEnt;
import pl.lodz.p.model.domain.Rent;

import java.util.List;
import java.util.UUID;

public class JpaRentRepository {
    private final EntityManagerFactory emf;

    public JpaRentRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public RentEnt saveRent(RentEnt rentEnt, String roomNumber, String guestPersonalId) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();

            try {
                transaction.begin();
                RoomEnt roomEnt = findRoomByNumber(roomNumber, em);
                GuestEnt guestEnt = findGuestByPersonalId(guestPersonalId, em);

                if (roomEnt.isRented() || roomEnt.isArchived()) {
                    throw new ConcurrentOperationException("Pokój o numerze " + roomEnt.getNumber() + " został już wynajęty lub zarchiwizowany");
                }

                rentEnt.setGuest(guestEnt);
                rentEnt.setRoom(roomEnt);
                roomEnt.setRented(true);
                em.persist(rentEnt);
                transaction.commit();
                return rentEnt;
            } catch (ConcurrentOperationException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw e;
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RepositoryException("Wystąpił błąd przy zapisie rezerwacji o UUID = " + rentEnt.getUuid() + ". " + e.getMessage());
            }

        }
    }

    public RentEnt findRentByUuid(UUID uuid) {
        try (EntityManager em = emf.createEntityManager()) {
            return findRentByUuid(uuid, em);
        }
    }

    private RentEnt findRentByUuid(UUID uuid, EntityManager em) {
        return (RentEnt) em.createNativeQuery(
                        "SELECT * FROM RENTS WHERE entity_uuid = :uuid", RentEnt.class)
                .setParameter("uuid", uuid)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono rezerwacji o UUID = " + uuid));
    }

    public List<RentEnt> findAllRents() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT g FROM Rent g", RentEnt.class)
                    .getResultList();
        }
    }

    public List<RentEnt> findGuestRents(String personalId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("select r from Rent r where r.guest.personalId = :personalId", RentEnt.class)
                    .setParameter("personalId", personalId)
                    .getResultList();
        }
    }

    public void endRent(UUID uuid) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();

            try {
                transaction.begin();
                RentEnt rentEnt = findRentByUuid(uuid, em);

                if (!rentEnt.isActive()) {
                    throw new ConcurrentOperationException("Rezerwacja o UUID = " + uuid + " została już zakończona");
                }

                RoomEnt roomEnt = rentEnt.getRoom();

                em.lock(rentEnt, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                em.lock(roomEnt, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                rentEnt.setActive(false);
                roomEnt.setRented(false);

                transaction.commit();
            } catch (EntityNotFoundException | ConcurrentOperationException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw e;
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                if (e instanceof OptimisticLockException || e.getCause() instanceof OptimisticLockException) {
                    throw new ConcurrentOperationException("Wykryto współbieżną modyfikację rezerwacji o UUID = " + uuid + ", " + e.getMessage());
                } else {
                    throw new RepositoryException("Wystąpił błąd przy zakończeniu rezerwacji o UUID = " + uuid + ", " + e.getMessage());
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

    private RoomEnt findRoomByNumber(String number, EntityManager em) {
        return em.createQuery("SELECT r FROM Room r WHERE r.number = :number", RoomEnt.class)
                .setParameter("number", number)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono pokoju o numerze = " + number));
    }
}
