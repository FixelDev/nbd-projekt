package pl.lodz.p;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import pl.lodz.p.adapter.JpaGuestRepositoryAdapter;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.domain.Address;
import pl.lodz.p.model.domain.Guest;
import pl.lodz.p.model.domain.GuestType;
import pl.lodz.p.model.domain.GuestTypeDefault;
import pl.lodz.p.repository.JpaGuestRepository;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Address address = new Address("bla", "bla", "blaa");
        GuestType guestType = new GuestTypeDefault();

        Guest guest = new Guest("Jan", "Paweł", "2137", address, guestType);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("nbd-pu");

        JpaGuestRepository rep = new JpaGuestRepository(emf);
        JpaGuestRepositoryAdapter adapter = new JpaGuestRepositoryAdapter(rep);

        try {
            adapter.saveGuest(guest);
        } catch (RepositoryException e) {
            System.err.println(e.getMessage());
        }

        IO.println(guest);
    }
}
