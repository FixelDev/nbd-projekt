package pl.lodz.p.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.GuestEnt;
import pl.lodz.p.model.db.RentEnt;
import pl.lodz.p.model.db.RoomEnt;

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

    private GuestEnt findGuestByPersonalId(String personalId, EntityManager em) {
        return (GuestEnt) em.createNativeQuery(
                        "SELECT * FROM GUESTS WHERE PERSONAL_ID = :personalId", GuestEnt.class)
                .setParameter("personalId", personalId)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono gościa o personalId = " + personalId));
    }

    private RoomEnt findRoomByNumber(String number, EntityManager em) {
        return (RoomEnt) em.createNativeQuery(
                        "SELECT * FROM ROOMS WHERE NUMBER = :number FOR NO KEY UPDATE", RoomEnt.class)
                .setParameter("number", number)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono pokoju o numerze = " + number));
    }
}
