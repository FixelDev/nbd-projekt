package pl.lodz.p;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import pl.lodz.p.model.domain.Guest;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        Guest guest = new Guest("Jan", "Paweł", "2137", false, null, null);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("nbd-pu");

        JpaGuestRepository rep = new JpaGuestRepository(emf);
        JpaClientRepositoryAdapter adapter = new JpaClientRepositoryAdapter(rep);

        adapter.saveGuest(guest);
    }
}
