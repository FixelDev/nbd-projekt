package pl.lodz.p;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Persistence;
import pl.lodz.p.adapter.JpaGuestRepositoryAdapter;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.JpaGuestRepository;

import java.util.List;
import java.util.UUID;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Address address = new Address("dasdas", "oooo", "2a");
        GuestType guestType = new GuestTypeExtra();
        Guest guest = new Guest("oh", "myszeczko", "1234", address, guestType);


        EntityManagerFactory emf = Persistence.createEntityManagerFactory("nbd-pu");

        JpaGuestRepository rep = new JpaGuestRepository(emf);
        JpaGuestRepositoryAdapter adapter = new JpaGuestRepositoryAdapter(rep);

//        Guest guest = adapter.findGuestByUUID(UUID.fromString("97c98b58-5455-45dd-a08e-b0626407c746"));

        try {
            adapter.saveGuest(guest);
        } catch (RepositoryException e) {
            System.err.println(e.getMessage());
        }
//
//       IO.println(guest);

//        try {
//            List<Guest> guests = adapter.findAllGuests();
//
//            for (Guest guest : guests) {
//                IO.println(guest);
//            }
//
//        } catch (EntityNotFoundException | ConcurrentOperationException | RepositoryException e) {
//            System.err.println(e.getMessage());
//        }
    }
}
